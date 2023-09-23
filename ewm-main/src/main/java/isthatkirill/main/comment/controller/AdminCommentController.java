package isthatkirill.main.comment.controller;

import isthatkirill.main.comment.dto.ResponseCommentDto;
import isthatkirill.main.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseCommentDto getByIdByAdmin(@PathVariable Long commentId) {
        return commentService.getByIdByAdmin(commentId);
    }


    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByIdByAdmin(@PathVariable Long commentId) {
        commentService.deleteByIdByAdmin(commentId);
    }

}
