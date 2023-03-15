package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IMatchDAO;
import dst.ass1.jpa.model.IMatch;
import dst.ass1.jpa.model.impl.Match;

import javax.persistence.EntityManager;
import java.util.Date;

public class MatchDAO extends FinderDaoImpl<IMatch, Match> implements IMatchDAO {

    public MatchDAO(EntityManager em) {
        super(Match.class, em);
    }

    @Override
    public long countMatchesByDate(Date date) {
        return 0;
    }
}
