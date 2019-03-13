package ru.blaj.workspacetraffic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.CamImage;
import ru.blaj.workspacetraffic.service.CamImageService;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.datasource.url=jdbc:postgresql://localhost:5432/workspace-traffic"})
@ActiveProfiles("test")
public class CamImageServiceUnitTest {
    @Autowired
    public CamImageService camImageService;

    @Test
    public void testGetAllImages(){
        String start = null;
        Page<CamImage> pageCollection = camImageService.getAllByCameraId(1L, start, start, 0,5);
        System.out.println(pageCollection);
    }
}
