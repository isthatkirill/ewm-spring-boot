package isthatkirill.main.comment.controller;

import isthatkirill.main.comment.dto.ResponseCommentDto;
import isthatkirill.main.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/comments/{eventId}")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<ResponseCommentDto> getAllCommentsForEvent(@PathVariable Long eventId,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                           @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return commentService.getAllCommentsForEvent(eventId, keyword, from, size);
    }

}
