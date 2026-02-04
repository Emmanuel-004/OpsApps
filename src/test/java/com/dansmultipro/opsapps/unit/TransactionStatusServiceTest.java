package com.dansmultipro.opsapps.unit;

import com.dansmultipro.opsapps.dto.transactionstatus.TransactionStatusResponseDto;
import com.dansmultipro.opsapps.model.TransactionStatus;
import com.dansmultipro.opsapps.repository.TransactionStatusRepository;
import com.dansmultipro.opsapps.service.impl.TransactionStatusServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TransactionStatusServiceTest {

    @Mock
    TransactionStatusRepository transactionStatusRepository;

    @InjectMocks
    TransactionStatusServiceImpl transactionStatusService;

    @Test
    public  void shouldReturnData_whenRequestValid(){
        TransactionStatus transactionStatus1 = new TransactionStatus();
        transactionStatus1.setName("OK");

        TransactionStatus transactionStatus2 = new TransactionStatus();
        transactionStatus2.setName("NO");

        List<TransactionStatus> transactionStatusList = List.of(transactionStatus1,transactionStatus2);

        Mockito.when(transactionStatusRepository.findAll()).thenReturn(transactionStatusList);

        List<TransactionStatusResponseDto> result = transactionStatusService.getAll();

        Assertions.assertEquals(transactionStatusList.size(), result.size());
    }
}
