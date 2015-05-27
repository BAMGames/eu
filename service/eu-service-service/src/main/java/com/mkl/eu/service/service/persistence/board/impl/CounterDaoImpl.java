package com.mkl.eu.service.service.persistence.board.impl;

import com.mkl.eu.service.service.persistence.board.ICounterDao;
import com.mkl.eu.service.service.persistence.impl.GenericDaoImpl;
import com.mkl.eu.service.service.persistence.oe.board.CounterEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of the Counter DAO.
 *
 * @author MKL.
 */
@Repository
public class CounterDaoImpl extends GenericDaoImpl<CounterEntity, Long> implements ICounterDao {
    /**
     * Constructor.
     */
    public CounterDaoImpl() {
        super(CounterEntity.class);
    }

    /** {@inheritDoc} */
    @Override
    public CounterEntity getCounter(Long idCounter, Long idGame) {
        CounterEntity counter = load(idCounter);

        if (idGame == null || counter == null || counter.getOwner() == null
                || counter.getOwner().getGame() == null
                || counter.getOwner().getGame().getId().longValue() != idGame.longValue()) {
            counter = null;
        }

        return counter;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getPatrons(String country, Long idGame) {
        List<String> countries;
        Criteria criteria = getSession().createCriteria(CounterEntity.class);

        criteria.add(Restrictions.or(Restrictions.eq("type", "DIPLOMACY"), Restrictions.eq("type", "DIPLOMACY_WAR")));
        criteria.add(Restrictions.eq("country", country));
        criteria.add(Restrictions.eq("owner.game.id", idGame));

        CounterEntity counter = (CounterEntity) criteria.uniqueResult();
        if (counter != null) {
            countries = new ArrayList<>();
            String box = counter.getOwner().getProvince();
            Matcher m = Pattern.compile("B_DE_([a-zA-Z])\\-.*").matcher(box);
            if (m.matches()) {
                countries.add(m.group(1));
            }
        } else {
            criteria = getSession().createCriteria(CounterEntity.class);

            criteria.add(Restrictions.or(Restrictions.eq("type", "ROTW_RELATION"), Restrictions.eq("type", "ROTW_ALLIANCE")));
            criteria.add(Restrictions.eq("owner.province", "B_DR_" + country));
            criteria.add(Restrictions.eq("owner.game.id", idGame));

            @SuppressWarnings("unchecked") List<CounterEntity> counters = criteria.list();

            countries = counters.stream().map(CounterEntity::getCountry).collect(Collectors.toList());
        }

        return countries;
    }
}
