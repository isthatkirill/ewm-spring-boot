package isthatkirill.main.comment.mapper;

import isthatkirill.main.comment.dto.RequestCommentDto;
import isthatkirill.main.comment.dto.ResponseCommentDto;
import isthatkirill.main.comment.model.Comment;
import isthatkirill.main.event.model.Event;
import isthatkirill.main.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "id", ignore = true)
    Comment toComment(RequestCommentDto requestCommentDto, User user, Event event);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    ResponseCommentDto toResponseCommentDto(Comment comment);

    List<ResponseCommentDto> toResponseCommentDto(List<Comment> comments);

}
