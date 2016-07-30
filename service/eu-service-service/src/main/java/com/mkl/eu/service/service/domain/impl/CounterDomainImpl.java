package com.mkl.eu.service.service.domain.impl;

import com.mkl.eu.client.common.util.CommonUtil;
import com.mkl.eu.client.service.vo.enumeration.CounterFaceTypeEnum;
import com.mkl.eu.client.service.vo.enumeration.DiffAttributeTypeEnum;
import com.mkl.eu.client.service.vo.enumeration.DiffTypeEnum;
import com.mkl.eu.client.service.vo.enumeration.DiffTypeObjectEnum;
import com.mkl.eu.service.service.domain.ICounterDomain;
import com.mkl.eu.service.service.persistence.board.ICounterDao;
import com.mkl.eu.service.service.persistence.board.IStackDao;
import com.mkl.eu.service.service.persistence.diff.IDiffDao;
import com.mkl.eu.service.service.persistence.oe.GameEntity;
import com.mkl.eu.service.service.persistence.oe.board.CounterEntity;
import com.mkl.eu.service.service.persistence.oe.board.StackEntity;
import com.mkl.eu.service.service.persistence.oe.diff.DiffAttributesEntity;
import com.mkl.eu.service.service.persistence.oe.diff.DiffEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Cross service class for counter manipulation.
 *
 * @author MKL.
 */
@Component
public class CounterDomainImpl implements ICounterDomain {
    /** Counter DAO. */
    @Autowired
    private ICounterDao counterDao;
    /** Stack DAO. */
    @Autowired
    private IStackDao stackDao;
    /** Diff DAO. */
    @Autowired
    private IDiffDao diffDao;

    /** {@inheritDoc} */
    @Override
    public DiffEntity createCounter(CounterFaceTypeEnum type, String country, String province, GameEntity game) {
        return createAndGetCounter(type, country, province, game).getLeft();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<DiffEntity, CounterEntity> createAndGetCounter(CounterFaceTypeEnum type, String country, String province, GameEntity game) {
        StackEntity stack = new StackEntity();
        stack.setProvince(province);
        stack.setGame(game);

        CounterEntity counterEntity = new CounterEntity();
        counterEntity.setCountry(country);
        counterEntity.setType(type);
        counterEntity.setOwner(stack);

        stack.getCounters().add(counterEntity);

        /*
         Thanks Hibernate to have 7 years old bugs.
         https://hibernate.atlassian.net/browse/HHH-6776
         https://hibernate.atlassian.net/browse/HHH-7404
          */

        stackDao.create(stack);

        game.getStacks().add(stack);

        DiffEntity diff = new DiffEntity();
        diff.setIdGame(game.getId());
        diff.setVersionGame(game.getVersion());
        diff.setType(DiffTypeEnum.ADD);
        diff.setTypeObject(DiffTypeObjectEnum.COUNTER);
        diff.setIdObject(counterEntity.getId());
        DiffAttributesEntity diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.PROVINCE);
        diffAttributes.setValue(province);
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.TYPE);
        diffAttributes.setValue(type.name());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.COUNTRY);
        diffAttributes.setValue(country);
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.STACK);
        diffAttributes.setValue(stack.getId().toString());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);

        diffDao.create(diff);

        return new ImmutablePair<>(diff, counterEntity);
    }

    /** {@inheritDoc} */
    @Override
    public DiffEntity removeCounter(Long idCounter, GameEntity game) {
        CounterEntity counter = CommonUtil.findFirst(game.getStacks().stream().flatMap(s -> s.getCounters().stream()),
                c -> c.getId().equals(idCounter));

        if (counter == null) {
            return null;
        }

        StackEntity stack = counter.getOwner();
        stack.getCounters().remove(counter);
        counter.setOwner(null);
        counterDao.delete(counter);

        if (stack.getCounters().isEmpty()) {
            stack.setGame(null);
            game.getStacks().remove(stack);
        }

        DiffEntity diff = new DiffEntity();
        diff.setIdGame(game.getId());
        diff.setVersionGame(game.getVersion());
        diff.setType(DiffTypeEnum.REMOVE);
        diff.setTypeObject(DiffTypeObjectEnum.COUNTER);
        diff.setIdObject(idCounter);
        DiffAttributesEntity diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.PROVINCE);
        diffAttributes.setValue(stack.getProvince());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);

        if (stack.getCounters().isEmpty()) {
            diffAttributes = new DiffAttributesEntity();
            diffAttributes.setType(DiffAttributeTypeEnum.STACK_DEL);
            diffAttributes.setValue(stack.getId().toString());
            diffAttributes.setDiff(diff);
            diff.getAttributes().add(diffAttributes);
        }

        diffDao.create(diff);

        return diff;
    }

    /** {@inheritDoc} */
    @Override
    public DiffEntity switchCounter(Long idCounter, CounterFaceTypeEnum type, GameEntity game) {
        Pair<DiffEntity, CounterEntity> pair = switchAndGetCounter(idCounter, type, game);

        if (pair == null) {
            return null;
        }

        return pair.getLeft();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<DiffEntity, CounterEntity> switchAndGetCounter(Long idCounter, CounterFaceTypeEnum type, GameEntity game) {
        CounterEntity counter = CommonUtil.findFirst(game.getStacks().stream().flatMap(s -> s.getCounters().stream()),
                c -> c.getId().equals(idCounter));

        if (counter == null) {
            return null;
        }

        counter.setType(type);

        DiffEntity diff = new DiffEntity();
        diff.setIdGame(game.getId());
        diff.setVersionGame(game.getVersion());
        diff.setType(DiffTypeEnum.MODIFY);
        diff.setTypeObject(DiffTypeObjectEnum.COUNTER);
        diff.setIdObject(idCounter);
        DiffAttributesEntity diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.TYPE);
        diffAttributes.setValue(type.name());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.PROVINCE);
        diffAttributes.setValue(counter.getOwner().getProvince());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);

        diffDao.create(diff);

        return new ImmutablePair<>(diff, counter);
    }

    /** {@inheritDoc} */
    @Override
    public DiffEntity changeVeteransCounter(Long idCounter, Integer veterans, GameEntity game) {
        CounterEntity counter = CommonUtil.findFirst(game.getStacks().stream().flatMap(s -> s.getCounters().stream()),
                c -> c.getId().equals(idCounter));

        if (counter == null) {
            return null;
        }

        counter.setVeterans(veterans);

        DiffEntity diff = new DiffEntity();
        diff.setIdGame(game.getId());
        diff.setVersionGame(game.getVersion());
        diff.setType(DiffTypeEnum.MODIFY);
        diff.setTypeObject(DiffTypeObjectEnum.COUNTER);
        diff.setIdObject(idCounter);
        DiffAttributesEntity diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.VETERANS);
        diffAttributes.setValue(veterans.toString());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.PROVINCE);
        diffAttributes.setValue(counter.getOwner().getProvince());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);

        diffDao.create(diff);

        return diff;
    }

    /** {@inheritDoc} */
    @Override
    public DiffEntity moveSpecialCounter(CounterFaceTypeEnum type, String country, String province, GameEntity game) {
        CounterEntity counter = CommonUtil.findFirst(game.getStacks().stream().flatMap(s -> s.getCounters().stream()),
                c -> StringUtils.equals(country, c.getCountry()) && c.getType() == type);

        if (counter == null) {
            return null;
        }

        StackEntity stack = CommonUtil.findFirst(game.getStacks().stream(), s -> StringUtils.equals(province, s.getProvince()));
        if (stack == null) {
            stack = new StackEntity();
            stack.setProvince(province);
            stack.setGame(game);

            /**
             Thanks Hibernate to have 7 years old bugs.
             https://hibernate.atlassian.net/browse/HHH-6776
             https://hibernate.atlassian.net/browse/HHH-7404
             */

            stackDao.create(stack);

            game.getStacks().add(stack);
        }

        DiffEntity diff = new DiffEntity();
        diff.setIdGame(game.getId());
        diff.setVersionGame(game.getVersion());
        diff.setType(DiffTypeEnum.MOVE);
        diff.setTypeObject(DiffTypeObjectEnum.COUNTER);
        diff.setIdObject(counter.getId());
        DiffAttributesEntity diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.STACK_FROM);
        diffAttributes.setValue(counter.getOwner().getId().toString());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.STACK_TO);
        diffAttributes.setValue(stack.getId().toString());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.PROVINCE_FROM);
        diffAttributes.setValue(counter.getOwner().getProvince());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        diffAttributes = new DiffAttributesEntity();
        diffAttributes.setType(DiffAttributeTypeEnum.PROVINCE_TO);
        diffAttributes.setValue(stack.getProvince());
        diffAttributes.setDiff(diff);
        diff.getAttributes().add(diffAttributes);
        if (counter.getOwner().getCounters().size() == 1) {
            diffAttributes = new DiffAttributesEntity();
            diffAttributes.setType(DiffAttributeTypeEnum.STACK_DEL);
            diffAttributes.setValue(counter.getOwner().getId().toString());
            diffAttributes.setDiff(diff);
            diff.getAttributes().add(diffAttributes);
        }

        diffDao.create(diff);

        StackEntity oldStack = counter.getOwner();
        counter.setOwner(stack);
        oldStack.getCounters().remove(counter);
        stack.getCounters().add(counter);
        if (oldStack.getCounters().isEmpty()) {
            oldStack.setGame(null);
            game.getStacks().remove(oldStack);
        }

        return diff;
    }
}
