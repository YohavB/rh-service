package com.yb.rh.utils

import com.fasterxml.jackson.annotation.JsonUnwrapped

open class RHResponse

class SuccessResponse<T>(@JsonUnwrapped val entity: T) : RHResponse()
