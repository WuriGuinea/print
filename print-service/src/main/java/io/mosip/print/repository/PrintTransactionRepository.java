package io.mosip.print.repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.print.entity.PrintTranactionEntity;
import org.springframework.stereotype.Repository;

@Repository("printTransactionRepository")
public interface PrintTransactionRepository extends BaseRepository<PrintTranactionEntity, String> {
    
}
