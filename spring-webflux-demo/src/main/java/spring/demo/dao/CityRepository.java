package spring.demo.dao;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;
import spring.demo.domain.City;

/**
 * 存储层
 * 使用ConcurrentMap模拟数据库
 * @author luoruihua
 * @date 2020/12/10
 */
@Repository
public class CityRepository {

    private final ConcurrentMap<Long, City> repository = new ConcurrentHashMap<>();

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    public Long save(final City city) {
        final Long id = ID_GENERATOR.incrementAndGet();
        city.setId(id);
        this.repository.put(id, city);
        return id;
    }

    public Collection<City> findAll() {
        return this.repository.values();
    }


    public City findCityById(final Long id) {
        return this.repository.get(id);
    }

    public Long updateCity(final City city) {
        this.repository.put(city.getId(), city);
        return city.getId();
    }

    public Long deleteCity(final Long id) {
        this.repository.remove(id);
        return id;
    }
}
