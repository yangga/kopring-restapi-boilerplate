package com.yangga.kopringrestapiboilerplate.common.payload

import com.fasterxml.jackson.annotation.JsonInclude
import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<out DATA>(
    @Schema(description = "data") @field: Valid val data: DATA,
    @Schema(description = "code") @field: Valid val code: String?
) {
    companion object {
        fun <DATA> success(data: DATA, code: String? = null) = ApiResponse(data, code)

        fun code(code: String) = Code(code)

        fun <DATA> list(data: kotlin.collections.List<DATA>, total: Int, code: String? = null) = List(data, total, data.size, code)

        fun error(code: ErrorCode, message: String, data: Any? = null) = Error(code, message, data)
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class List<out DATA>(
        @Schema(description = "data") @field: Valid val data: kotlin.collections.List<DATA>,
        @Schema(description = "total") @field: Min(0) val total: Int,
        @Schema(description = "count") @field: Min(0) val count: Int,
        @Schema(description = "code") @field: Valid val code: String?,
    )

    data class Code(
        @Schema(description = "code") @field: Valid val code: String
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Error(
        @Schema(description = "code") @field: NotNull val code: ErrorCode,
        @Schema(description = "error message") @field: NotNull val message: String,
        @Schema(description = "error data") val data: Any?
    )

}
