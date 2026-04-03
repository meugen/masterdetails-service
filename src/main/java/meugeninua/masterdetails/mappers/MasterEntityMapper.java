package meugeninua.masterdetails.mappers;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.entities.Master;
import org.mapstruct.Mapper;

@Mapper(uses = DetailEntityMapper.class)
public interface MasterEntityMapper {
    Master mapToEntity(MasterDto dto);
    MasterDto mapToDto(Master entity);
}
