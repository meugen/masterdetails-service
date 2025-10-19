package meugeninua.masterdetails.mappers;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.entities.Master;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MasterEntityMapper {
    Master mapToEntity(MasterDto dto);
    MasterDto mapToDto(Master entity);
}
