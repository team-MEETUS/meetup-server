package site.mymeetup.meetupserver.common.service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageService {
    private final DefaultMessageService messageService;

    public MessageService(@Value("${coolsms.api.key}") String apikey,
                          @Value("${coolsms.api.secret}") String apiSecret) {
        this.messageService = NurigoApp.INSTANCE.initialize(apikey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendOne(Message message) {
        messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
