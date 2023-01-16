package com.yb.rh.services

import io.github.jav.exposerversdk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.stream.Collectors

@Service
class NotificationService {
    fun sendPushNotification(pushToken: String, blockedCarPlate: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val recipient = "ExponentPushToken[$pushToken]"
            val title = if (blockedCarPlate != null) "Sorry but you need to move"
            else "You've been blocked !"
            val message = if (blockedCarPlate != null) "Car : $blockedCarPlate need to go, please move your car"
            else "You've been blocked, remember to use the Let Me Go button."

            if (!PushClient.isExponentPushToken(recipient)) throw Error("Token:$recipient is not a valid token.")
            val expoPushMessage = ExpoPushMessage()
            expoPushMessage.to.add(recipient)
            expoPushMessage.title = title
            expoPushMessage.body = message
            val expoPushMessages: MutableList<ExpoPushMessage> = ArrayList()
            expoPushMessages.add(expoPushMessage)
            val client = PushClient()
            val chunks = client.chunkPushNotifications(expoPushMessages)
            val messageRepliesFutures: MutableList<CompletableFuture<List<ExpoPushTicket>>> = ArrayList()
            for (chunk: List<ExpoPushMessage?>? in chunks) {
                messageRepliesFutures.add(client.sendPushNotificationsAsync(chunk))
            }

            // Wait for each completable future to finish
            val allTickets: MutableList<ExpoPushTicket> = ArrayList()
            for (messageReplyFuture: CompletableFuture<List<ExpoPushTicket>> in messageRepliesFutures) {
                try {
                    for (ticket: ExpoPushTicket in withContext(Dispatchers.IO) {
                        messageReplyFuture.get()
                    }) {
                        allTickets.add(ticket)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }
            }
            val zippedMessagesTickets = client.zipMessagesTickets(expoPushMessages, allTickets)
            val okTicketMessages = client.filterAllSuccessfulMessages(zippedMessagesTickets)
            val okTicketMessagesString: String = okTicketMessages.stream()
                .map { p: ExpoPushMessageTicketPair<ExpoPushMessage> -> "Title: " + p.message.title + ", Id:" + p.ticket.id }
                .collect(Collectors.joining(","))
            println(
                "Received OK ticket for " +
                        okTicketMessages.size +
                        " messages: " + okTicketMessagesString
            )
            val errorTicketMessages = client.filterAllMessagesWithError(zippedMessagesTickets)
            val errorTicketMessagesString: String =
                errorTicketMessages.stream().map { p: ExpoPushMessageTicketPair<ExpoPushMessage> ->
                    "Title: " + p.message.title + ", Error: " + p.ticket.details.error
                }.collect(Collectors.joining(","))
            println(
                ("Received ERROR ticket for " +
                        errorTicketMessages.size +
                        " messages: " +
                        errorTicketMessagesString)
            )


            // Countdown 30s
            val wait = 30
            for (i in wait downTo 0) {
                print("Waiting for " + wait + " seconds. " + i + "s\r")
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
            }
            println("Fetching receipts...")
            val ticketIds = (client.getTicketIdsFromPairs(okTicketMessages))
            val receiptFutures = client.getPushNotificationReceiptsAsync(ticketIds)
            var receipts: List<ExpoPushReceipt> = ArrayList()
            try {
                receipts = withContext(Dispatchers.IO) {
                    receiptFutures.get()
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            println(
                "Received " + receipts.size + " receipts:"
            )
            for (receipt: ExpoPushReceipt in receipts) {
                println(
                    ("Receipt for id: " +
                            receipt.id +
                            " had status: " +
                            receipt.status)
                )
            }
        }
    }
}