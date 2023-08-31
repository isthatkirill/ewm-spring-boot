package isthatkirill.main.compilation.controller;

import isthatkirill.main.compilation.dto.CompilationRequestDto;
import isthatkirill.main.compilation.dto.CompilationResponseDto;
import isthatkirill.main.compilation.service.CompilationService;
import isthatkirill.main.validation.group.OnCreate;
import isthatkirill.main.validation.group.OnUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto create(@RequestBody @Validated(OnCreate.class) CompilationRequestDto compilationRequestDto) {
        return compilationService.create(compilationRequestDto);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto update(@RequestBody @Validated(OnUpdate.class) CompilationRequestDto compilationRequestDto,
                                         @PathVariable Long compId) {
        return compilationService.update(compilationRequestDto, compId);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

}
