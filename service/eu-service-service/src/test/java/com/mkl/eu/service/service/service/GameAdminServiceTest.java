package com.mkl.eu.service.service.service;

import com.mkl.eu.client.common.exception.IConstantsCommonException;
import com.mkl.eu.client.common.exception.TechniqueException;
import com.mkl.eu.client.service.vo.diff.Diff;
import com.mkl.eu.client.service.vo.diff.DiffResponse;
import com.mkl.eu.client.service.vo.enumeration.DiffAttributeTypeEnum;
import com.mkl.eu.client.service.vo.enumeration.DiffTypeEnum;
import com.mkl.eu.client.service.vo.enumeration.DiffTypeObjectEnum;
import com.mkl.eu.service.service.mapping.diff.DiffMapping;
import com.mkl.eu.service.service.persistence.IGameDao;
import com.mkl.eu.service.service.persistence.diff.IDiffDao;
import com.mkl.eu.service.service.persistence.diff.board.IProvinceDao;
import com.mkl.eu.service.service.persistence.oe.GameEntity;
import com.mkl.eu.service.service.persistence.oe.board.BorderEntity;
import com.mkl.eu.service.service.persistence.oe.board.EuropeanProvinceEntity;
import com.mkl.eu.service.service.persistence.oe.board.StackEntity;
import com.mkl.eu.service.service.persistence.oe.diff.DiffEntity;
import com.mkl.eu.service.service.service.impl.GameAdminServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * Test of GameAdminService.
 *
 * @author MKL.
 */
@RunWith(MockitoJUnitRunner.class)
public class GameAdminServiceTest {
    @InjectMocks
    private GameAdminServiceImpl gameAdminService;

    @Mock
    private IGameDao gameDao;

    @Mock
    private IDiffDao diffDao;

    @Mock
    private IProvinceDao provinceDao;

    @Mock
    private DiffMapping diffMapping;

    /** Variable used to store something coming from a mock. */
    private DiffEntity diffEntity;

    @Test
    public void testMoveStackFailSimple() {
        Long idGame = 12L;
        Long versionGame = 1L;
        Long idStack = 4L;
        String provinceTo = "IdF";

        try {
            gameAdminService.moveStack(null, null, null, null);
            Assert.fail("Should break because idGame is null");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.NULL_PARAMETER, e.getCode());
            Assert.assertEquals("idGame", e.getParams()[0]);
        }

        try {
            gameAdminService.moveStack(idGame, null, null, null);
            Assert.fail("Should break because versionGame is null");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.NULL_PARAMETER, e.getCode());
            Assert.assertEquals("versionGame", e.getParams()[0]);
        }

        try {
            gameAdminService.moveStack(idGame, versionGame, null, null);
            Assert.fail("Should break because idStack is null");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.NULL_PARAMETER, e.getCode());
            Assert.assertEquals("idStack", e.getParams()[0]);
        }

        try {
            gameAdminService.moveStack(idGame, versionGame, idStack, null);
            Assert.fail("Should break because provinceTo is null");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.NULL_PARAMETER, e.getCode());
            Assert.assertEquals("provinceTo", e.getParams()[0]);
        }

        try {
            gameAdminService.moveStack(idGame, versionGame, idStack, provinceTo);
            Assert.fail("Should break because game does not exist");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.INVALID_PARAMETER, e.getCode());
            Assert.assertEquals("idGame", e.getParams()[0]);
        }

        GameEntity game = new GameEntity();
        game.setId(12L);

        when(gameDao.lock(12L)).thenReturn(game);

        try {
            gameAdminService.moveStack(idGame, versionGame, idStack, provinceTo);
            Assert.fail("Should break because versions does not match");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.INVALID_PARAMETER, e.getCode());
            Assert.assertEquals("versionGame", e.getParams()[0]);
        }
    }

    @Test
    public void testMoveStackFailComplex() {
        Long idGame = 12L;
        Long versionGame = 1L;
        Long idStack = 13L;
        String provinceTo = "IdF";

        EuropeanProvinceEntity idf = new EuropeanProvinceEntity();
        idf.setId(257L);
        idf.setName("IdF");

        EuropeanProvinceEntity pecs = new EuropeanProvinceEntity();
        pecs.setId(256L);
        pecs.setName("pecs");

        GameEntity game = new GameEntity();
        game.setId(12L);
        game.setVersion(5L);

        when(gameDao.lock(12L)).thenReturn(game);

        try {
            gameAdminService.moveStack(idGame, versionGame, idStack, provinceTo);
            Assert.fail("Should break because stack does not exist");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.INVALID_PARAMETER, e.getCode());
            Assert.assertEquals("idStack", e.getParams()[0]);
        }

        StackEntity stack = new StackEntity();
        stack.setProvince(pecs);
        stack.setId(14L);
        game.getStacks().add(stack);

        try {
            gameAdminService.moveStack(idGame, versionGame, idStack, provinceTo);
            Assert.fail("Should break because stack does not exist");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.INVALID_PARAMETER, e.getCode());
            Assert.assertEquals("idStack", e.getParams()[0]);
        }

        stack.setId(13L);

        try {
            gameAdminService.moveStack(idGame, versionGame, idStack, provinceTo);
            Assert.fail("Should break because province does not exist");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.INVALID_PARAMETER, e.getCode());
            Assert.assertEquals("provinceTo", e.getParams()[0]);
        }

        when(provinceDao.getProvinceByName("IdF")).thenReturn(idf);

        try {
            gameAdminService.moveStack(idGame, versionGame, idStack, provinceTo);
            Assert.fail("Should break because province is not close to former one");
        } catch (TechniqueException e) {
            Assert.assertEquals(IConstantsCommonException.INVALID_PARAMETER, e.getCode());
            Assert.assertEquals("provinceTo", e.getParams()[0]);
        }
    }

    @Test
    public void testMoveStackSuccess() {
        Long idGame = 12L;
        Long versionGame = 1L;
        Long idStack = 13L;
        String provinceTo = "IdF";

        EuropeanProvinceEntity idf = new EuropeanProvinceEntity();
        idf.setId(257L);
        idf.setName("IdF");

        EuropeanProvinceEntity pecs = new EuropeanProvinceEntity();
        pecs.setId(256L);
        pecs.setName("pecs");

        GameEntity game = new GameEntity();
        game.setId(12L);
        game.setVersion(5L);
        StackEntity stack = new StackEntity();
        stack.setProvince(pecs);
        stack.setId(13L);
        BorderEntity border = new BorderEntity();
        border.setProvinceFrom(pecs);
        border.setProvinceTo(idf);
        stack.getProvince().getBorders().add(border);
        game.getStacks().add(stack);

        when(gameDao.lock(12L)).thenReturn(game);

        when(provinceDao.getProvinceByName("IdF")).thenReturn(idf);

        List<DiffEntity> diffBefore = new ArrayList<>();
        diffBefore.add(new DiffEntity());
        diffBefore.add(new DiffEntity());

        when(diffDao.getDiffsSince(12L, 1L)).thenReturn(diffBefore);

        when(diffDao.create(anyObject())).thenAnswer(invocation -> {
            diffEntity = (DiffEntity) invocation.getArguments()[0];
            return diffEntity;
        });

        List<Diff> diffAfter = new ArrayList<>();
        diffAfter.add(new Diff());
        diffAfter.add(new Diff());

        when(diffMapping.oesToVos(anyObject())).thenReturn(diffAfter);

        DiffResponse response = gameAdminService.moveStack(idGame, versionGame, idStack, provinceTo);

        InOrder inOrder = inOrder(gameDao, provinceDao, diffDao, diffMapping);

        inOrder.verify(gameDao).lock(12L);
        inOrder.verify(diffDao).getDiffsSince(12L, 1L);
        inOrder.verify(provinceDao).getProvinceByName("IdF");
        inOrder.verify(diffDao).create(anyObject());
        inOrder.verify(diffMapping).oesToVos(anyObject());

        Assert.assertEquals(13L, diffEntity.getIdObject().longValue());
        Assert.assertEquals(DiffTypeEnum.MOVE, diffEntity.getType());
        Assert.assertEquals(DiffTypeObjectEnum.STACK, diffEntity.getTypeObject());
        Assert.assertEquals(12L, diffEntity.getIdGame().longValue());
        Assert.assertEquals(game.getVersion(), diffEntity.getVersionGame().longValue());
        Assert.assertEquals(2, diffEntity.getAttributes().size());
        Assert.assertEquals(DiffAttributeTypeEnum.PROVINCE_FROM, diffEntity.getAttributes().get(0).getType());
        Assert.assertEquals(pecs.getName(), diffEntity.getAttributes().get(0).getValue());
        Assert.assertEquals(DiffAttributeTypeEnum.PROVINCE_TO, diffEntity.getAttributes().get(1).getType());
        Assert.assertEquals(idf.getName(), diffEntity.getAttributes().get(1).getValue());

        Assert.assertEquals(game.getVersion(), response.getVersionGame().longValue());
        Assert.assertEquals(diffAfter, response.getDiffs());
    }
}
