package org.example.project.service.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DialogStateFactory {
    private final MainState mainState;
    private final NoteState noteState;
    private final GPTState gptState;
    private final LeetcodeState leetcodeState;
    private final InvestState investState;

    public DialogState getState(DialogMode mode) {
        return switch (mode) {
            case MAIN -> mainState;
            case NOTE -> noteState;
            case GPT -> gptState;
            case LEETCODE -> leetcodeState;
            case INVEST -> investState;
        };
    }
}
