package com.arconsis.data.email

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EmailRepository(
    @RestClient val emailApi: EmailApi
) {
    fun sendEmail(emailDto: EmailDto): Uni<Boolean> = emailApi.sendEmail(
        senderEmail = emailDto.senderEmail,
        receiverEmail = emailDto.receiverEmail,
        subject = emailDto.subject,
        text = emailDto.text
    )
        .map {
            true
        }
        .onFailure()
        .recoverWithItem(false)
}

data class EmailDto(
    val senderEmail: String,
    val receiverEmail: String,
    val subject: String,
    val text: String,
)