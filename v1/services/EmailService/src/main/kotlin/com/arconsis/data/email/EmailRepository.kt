package com.arconsis.data.email

import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EmailRepository(
    @RestClient val emailApi: EmailApi
) {
    suspend fun sendEmail(emailDto: EmailDto) {
        try {
            emailApi.sendEmail(
                senderEmail = emailDto.senderEmail,
                receiverEmail = emailDto.receiverEmail,
                subject = emailDto.subject,
                text = emailDto.text
            )
                .onFailure()
                .recoverWithNull()
                .awaitSuspending()
        } catch (e: Exception) {
            return
        }
    }
}

data class EmailDto(
    val senderEmail: String,
    val receiverEmail: String,
    val subject: String,
    val text: String,
)