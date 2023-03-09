package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IMatchDAO;
import dst.ass1.jpa.model.IMatch;

import java.util.Date;
import java.util.List;

public class MatchDAO implements IMatchDAO {
    @Override
    public IMatch findById(Long id) {
        return null;
    }

    @Override
    public List<IMatch> findAll() {
        return null;
    }

    @Override
    public long countMatchesByDate(Date date) {
        return 0;
    }
}
