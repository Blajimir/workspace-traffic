package ru.blaj.workspacetraffic.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.CamImage;
import ru.blaj.workspacetraffic.repository.CamImageRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Класс - сервис для взаимодействие с логами результатов когнетивного сервиса
 *
 *  @author Alesandr Kovalev aka blajimir
 * */
@Service
@Log
public class CamImageService {
    private CamImageRepository camImageRepository;

    @Autowired
    public CamImageService(CamImageRepository camImageRepository) {
        this.camImageRepository = camImageRepository;
    }

    /**
     * Эта функция добавляет объект логирования в БД. В качестве объекта модели который описывает таблицу в БД используется
     * CamImage {@link CamImage}
     *
     * @param camImage - объект класса {@link CamImage}, объект не должен быть {@literal null}
     * @return возвращает единицу логирования с полученными от когнетивного сервиса  данными в виде объекта {@link CamImage}
     */
    public CamImage addCamImage(@NotNull CamImage camImage) {
        if (camImage.getId() != null) {
            camImage.setId(null);
        }
        return this.camImageRepository.save(camImage);
    }

    /**
     * Эта функция сохраняет изменения в существующем объекте логирования в БД. В качестве ообъекта модели который
     * описывает таблицу в БД используется
     * CamImage {@link CamImage}
     *
     * @param camImage - объект класса обязательно должен содержать существующий в БД id {@link CamImage}, объект не должен быть {@literal null}
     * @return возвращает единицу логирования с полученными от когнетивного сервиса  данными в виде объекта {@link CamImage}
     */
    public CamImage saveCamImage(@NotNull CamImage camImage) {
        return Optional.of(camImage)
                .filter(ci -> ci.getId() != null && ci.getId() != 0)
                .map(ci -> this.camImageRepository.save(ci)).orElse(null);
    }

    /**
     * Эта функция позволяет получить единицу объекта логирования из БД в виде объекта {@link CamImage}
     *
     * @param id - объект класса обязательно должен содержать существующий в БД id {@link Long}, объект не должен быть {@literal null}
     * @return возвращает единицу логирования сохраненныую в БД {@link CamImage}
     */
    public CamImage getCamImage(@NotNull Long id) {
        return Optional.of(id).filter(aLong -> aLong != 0)
                .map(aLong -> this.camImageRepository.findById(aLong).orElse(null))
                .orElse(null);
    }

    /**
     * Эта функция позволяет получить все объекты логирования из БД в виде коллекции объектов {@link CamImage}
     *
     * @return возвращает единицу логирования сохраненныую в БД {@link CamImage}
     */
    public Collection<CamImage> getAllCamImage(){
        return Collections.unmodifiableCollection(this.camImageRepository.findAll());
    }

    /**
     * Эта функция позволяет получить объекты логирования из БД в виде коллекции объектов {@link CamImage}
     * с помощью параметров можно отфильтровать записи, а так же эта функция принимает параметры пагинации
     *
     * @param id - это идентификатор камеры, обязательный параметр, для фильтрации сообщений по id камеры
     * @param start- фильтр по дате в виде строки в формате yyyyMMddhhmmss, все записи больше или равные этому значению,
     *             необязательынй параметр, может быть равным null
     * @param end -   фильтр по дате в виде строки в формате yyyyMMddhhmmss, все записи меньше или равные этому значению,
     *            необязательынй параметр, может быть равным null
     * @param page - определяет номер страницы, используется для пагинации результатов
     * @param size - определяет количество записей в таблице, используется для пагинации результатов
     * @return возвращает коллекцию {@link Page} объектов логирования которые хранятся в БД в виде объектов {@link CamImage}
     */
    public Page<CamImage> getAllByCameraId(@NotNull Long id, String start, String end, int page, int size){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = start!=null?sdf.parse(start):null;
            endDate = end!=null?sdf.parse(end):null;
        } catch (ParseException e) {
            log.warning(e.getMessage());
        }
        return getAllByCameraId( id, startDate, endDate, page, size);
    }

    /**
     * Эта функция позволяет получить объекты логирования из БД в виде коллекции объектов {@link CamImage}
     * с помощью параметров можно отфильтровать записи, а так же эта функция принимает параметры пагинации
     *
     * @param id - это идентификатор камеры, обязательный параметр, для фильтрации сообщений по id камеры
     * @param startDate - фильтр по дате, все записипи больше или равные этому значению, необязательынй параметр,
     *                  может быть равным null
     * @param endDate -   фильтр по дате, все записипи меньше или равные этому значению, необязательынй параметр,
     *                  может быть равным null
     * @param page - определяет номер страницы, используется для пагинации результатов
     * @param size - определяет количество записей в таблице, используется для пагинации результатов
     * @return возвращает коллекцию {@link Page} объектов логирования которые хранятся в БД в виде объектов {@link CamImage}
     */
    public Page<CamImage> getAllByCameraId(@NotNull Long id, Date startDate, Date endDate, int page, int size){
        Page<CamImage> result = null;
        Date insertStartDate = Optional.ofNullable(startDate).orElseGet(() -> {
            Calendar c = Calendar.getInstance();
            c.set(1985,1,1);
            return c.getTime();
        });
        Date insertEndDate = Optional.ofNullable(endDate).orElseGet(() -> {
            Calendar c = Calendar.getInstance();
            c.set(2100,1,1);
            return c.getTime();
        });
        Pageable pageable = PageRequest.of(page,size, Sort.by("timestamp"));
        result = camImageRepository.findAllByCameraId(id, insertStartDate, insertEndDate, pageable);
        return result;
    }

    /**
     * Эта функция позволяет удалить запись из БД
     *
     * @param camImage - в качестве параметра принимается объект {@link CamImage}
     */
    public void deleteCamImage(@NotNull CamImage camImage) {
        this.camImageRepository.delete(camImage);
    }

    /**
     * Эта функция позволяет удалить запись из БД
     *
     * @param id - в качестве параметра принимается id
     */
    public void deleteCamImage(@NotNull Long id) {
        if (this.camImageRepository.existsById(id)) {
            this.camImageRepository.deleteById(id);
        }
    }

    /**
     * Вспомогательный метод для удаления половины логов (@link {@link CamImage}), необходим для работы на тест-сервере
     * с ограниченным объемом хранилища, метод удаляет половину накопленных логов
     *
     */
    @Transactional
    public void deleteHalfOfCamImages(){
        long halfId = camImageRepository.getMaxId() - (camImageRepository.count()/2);
        if(halfId>0){
            camImageRepository.deleteAllByIdLessThanEqual(halfId);
        }
    }

    @Transactional
    public void deleteAllCamImages(){
        camImageRepository.deleteAll();
    }

    public long getCamImagesCount(){
        return camImageRepository.count();
    }
}
