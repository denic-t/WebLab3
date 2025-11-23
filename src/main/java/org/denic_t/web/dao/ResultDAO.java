package org.denic_t.web.dao;

import org.denic_t.web.entity.ResultEntity;
import java.util.List;

public interface ResultDAO {
    void addResult(ResultEntity result);

    List<ResultEntity> getAllResults();

    void clearResults();
}
