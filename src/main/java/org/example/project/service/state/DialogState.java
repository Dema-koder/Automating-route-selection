package org.example.project.service.state;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface DialogState {
    void handleUpdate(Update update);
}
