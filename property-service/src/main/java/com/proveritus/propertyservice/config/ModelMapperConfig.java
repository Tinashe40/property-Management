package com.proveritus.propertyservice.config;

import com.proveritus.propertyservice.dto.FloorDTO;
import com.proveritus.propertyservice.dto.UnitDTO;
import com.proveritus.propertyservice.entity.Floor;
import com.proveritus.propertyservice.entity.Unit;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        // Configure FloorDTO to Floor mapping
        modelMapper.typeMap(
                FloorDTO.class,
                Floor.class).addMappings(mapper -> {
            mapper.skip(Floor::setProperty);
            mapper.skip(Floor::setUnits);
        });

        // Configure UnitDTO to Unit mapping
        modelMapper.typeMap(
                UnitDTO.class,
                Unit.class).addMappings(mapper -> {
            mapper.skip(Unit::setProperty);
            mapper.skip(Unit::setFloor);
        });

        return modelMapper;
    }
}
