package ru.blaj.workspacetraffic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.blaj.workspacetraffic.model.DatasetUnitDto;
import ru.blaj.workspacetraffic.service.LoadDatasetService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("testdb")
public class LoadDatasetServiceUnitTest {
    @Autowired
    LoadDatasetService loadDatasetService;

    @Test
    public void testLoadDataSet(){

        List<DatasetUnitDto> dataSet = loadDatasetService.getDataSet();

        Assert.assertNotNull(dataSet);
        Assert.assertNotEquals(0, dataSet.size());

        System.out.println(dataSet.stream().limit(4).collect(Collectors.toList()));
    }

    @Test
    public void testSaveDataset() throws IOException {
        loadDatasetService.saveDataSetLikeZip();
    }
}
