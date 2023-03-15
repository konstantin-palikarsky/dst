package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IMatchDAO;
import dst.ass1.jpa.model.IMatch;
import dst.ass1.jpa.model.impl.Match;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

public class MatchDAO extends BasicDAOImpl<IMatch> implements IMatchDAO {

    public MatchDAO(EntityManager em) {
        super(Match.class, em);
    }

    @Override
    public long countMatchesByDate(Date date) {
        try {
            return this.em.createNamedQuery(Constants.Q_COUNT_MATCH_BY_DATE)
                    .setParameter("date", date).getResultList().size();
        } catch (NoResultException e) {
            return 0;
        }
    }
}
