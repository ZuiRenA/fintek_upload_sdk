package com.fintek.upload_sdk.model

data class BaseResponse<T> (
    val code: String? = null,
    val message: String? = null,
    val time: String? = null,
    val data: T? = null
): IBaseResponse {
    override val isSuccess: Boolean
        get() = code == "200"

    override val isFailed: Boolean
        get() = !isSuccess
}

data class UnitResponse(
    val code: String? = null,
    val message: String? = null,
): IBaseResponse {
    override val isSuccess: Boolean
        get() = code == "200"

    override val isFailed: Boolean
        get() = !isSuccess
}


interface IBaseResponse {
    val isSuccess: Boolean
    val isFailed: Boolean
}