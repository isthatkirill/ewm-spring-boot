package isthatkirill.main.compilation.service;

import isthatkirill.main.compilation.dto.CompilationRequestDto;
import isthatkirill.main.compilation.dto.CompilationResponseDto;

import java.util.List;

public interface CompilationService {

    CompilationResponseDto create(CompilationRequestDto compilationRequestDto);

    CompilationResponseDto update(CompilationRequestDto compilationRequestDto, Long compId);

    void delete(Long compId);

    CompilationResponseDto getById(Long compId);

    List<CompilationResponseDto> getAll(Boolean pinned, Integer from, Integer size);

}
