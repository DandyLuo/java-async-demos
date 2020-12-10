package spring.demo.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.demo.dao.CityRepository;
import spring.demo.domain.City;

/**
 * @author luoruihua
 * @date 2020/12/10
 */
@Component
public class CityHandler {

    private final CityRepository cityRepository;

    @Autowired
    public CityHandler(final CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public Mono<Long> save(final City city) {
        return Mono.create(cityMonoSink -> cityMonoSink.success(this.cityRepository.save(city)));
    }

    public Mono<City> findCityById(final Long id) {
        return Mono.justOrEmpty(this.cityRepository.findCityById(id));
    }

    public Flux<City> findAllCity() {
        return Flux.fromIterable(this.cityRepository.findAll());
    }

    public Mono<Long> modifyCity(final City city) {
        return Mono.create(cityMonoSink -> cityMonoSink.success(this.cityRepository.updateCity(city)));
    }

    public Mono<Long> deleteCity(final Long id) {
        return Mono.create(cityMonoSink -> cityMonoSink.success(this.cityRepository.deleteCity(id)));
    }
}
