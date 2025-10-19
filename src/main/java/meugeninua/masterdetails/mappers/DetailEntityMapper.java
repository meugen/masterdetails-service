package meugeninua.masterdetails.mappers;

import meugeninua.masterdetails.dto.DetailDto;
import meugeninua.masterdetails.entities.Detail;
import meugeninua.masterdetails.entities.Master;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface DetailEntityMapper {
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    Detail mapToEntity(Master master, DetailDto dto);
    default List<Detail> mapToEntityList(Master master, List<DetailDto> dtoList)  {
        return dtoList.stream()
            .map(dto -> mapToEntity(master, dto))
            .toList();
    }
    DetailDto mapToDto(Detail entity);
    List<DetailDto> mapToDtoList(List<Detail> entityList);
}
