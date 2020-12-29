package spring.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import spring.demo.domain.City;
import spring.demo.handler.CityHandler;

/**
 * 控制器
 * @author luoruihua
 * @date 2020/12/10
 */
@RestController
@RequestMapping(value = "/city")
public class CityWebFluxController {
    @Autowired
    private CityHandler cityHandler;

    @GetMapping(value = "/{id}")
    public Mono<City> findCityById(@PathVariable("id") final Long id) {
        return this.cityHandler.findCityById(id);
    }

    @GetMapping()
    public Flux<City> findAllCity() {
        return this.cityHandler.findAllCity();
    }

    @PostMapping()
    public Mono<Long> saveCity(@RequestBody final City city) {
        return this.cityHandler.save(city);
    }

    @PutMapping()
    public Mono<Long> modifyCity(@RequestBody final City city) {
        return this.cityHandler.modifyCity(city);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Long> deleteCity(@PathVariable("id") final Long id) {
        return this.cityHandler.deleteCity(id);
    }
}
