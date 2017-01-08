package com.mkl.eu.service.service.mapping;

import com.mkl.eu.client.service.service.eco.EconomicalSheetCountry;
import com.mkl.eu.client.service.vo.Game;
import com.mkl.eu.client.service.vo.GameLight;
import com.mkl.eu.client.service.vo.board.Counter;
import com.mkl.eu.client.service.vo.board.Stack;
import com.mkl.eu.client.service.vo.country.Monarch;
import com.mkl.eu.client.service.vo.country.PlayableCountry;
import com.mkl.eu.client.service.vo.country.Relation;
import com.mkl.eu.client.service.vo.eco.AdministrativeAction;
import com.mkl.eu.client.service.vo.eco.EconomicalSheet;
import com.mkl.eu.client.service.vo.enumeration.*;
import com.mkl.eu.client.service.vo.event.PoliticalEvent;
import com.mkl.eu.service.service.mapping.eco.EconomicalSheetMapping;
import com.mkl.eu.service.service.persistence.oe.GameEntity;
import com.mkl.eu.service.service.persistence.oe.board.CounterEntity;
import com.mkl.eu.service.service.persistence.oe.board.StackEntity;
import com.mkl.eu.service.service.persistence.oe.country.MonarchEntity;
import com.mkl.eu.service.service.persistence.oe.country.PlayableCountryEntity;
import com.mkl.eu.service.service.persistence.oe.country.RelationEntity;
import com.mkl.eu.service.service.persistence.oe.eco.AdministrativeActionEntity;
import com.mkl.eu.service.service.persistence.oe.eco.EconomicalSheetEntity;
import com.mkl.eu.service.service.persistence.oe.event.PoliticalEventEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

/**
 * Test of GameMapping.
 *
 * @author MKL.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/com/mkl/eu/service/service/mapping/test-eu-mapping-applicationContext..xml"})
public class GameMappingTest {
    private static final PlayableCountry FRA_VO;
    private static final PlayableCountry PRU_VO;
    private static final PlayableCountryEntity FRA_OE;
    private static final PlayableCountryEntity PRU_OE;
    private static final String PECS = "Pecs";
    private static final String TYR = "Tyr";
    private static final String IDF = "IdF";
    @Autowired
    private GameMapping gameMapping;
    @Autowired
    private EconomicalSheetMapping economicalSheetMapping;

    static {
        FRA_VO = new PlayableCountry();
        FRA_VO.setId(1L);
        FRA_VO.setName("FRA");
        FRA_VO.setUsername("MKL");
        FRA_VO.setDti(3);
        FRA_VO.setColonisationPenalty(3);
        FRA_VO.setReady(true);

        PRU_VO = new PlayableCountry();
        PRU_VO.setId(2L);
        PRU_VO.setName("PRU");
        PRU_VO.setUsername("Fogia");
        PRU_VO.setFti(4);
        PRU_VO.setFtiRotw(4);
        PRU_VO.setReady(false);

        FRA_OE = new PlayableCountryEntity();
        FRA_OE.setId(1L);
        FRA_OE.setName("FRA");
        FRA_OE.setUsername("MKL");
        FRA_OE.setDti(3);
        FRA_OE.setColonisationPenalty(3);
        FRA_OE.setReady(true);

        PRU_OE = new PlayableCountryEntity();
        PRU_OE.setId(2L);
        PRU_OE.setName("PRU");
        PRU_OE.setUsername("Fogia");
        PRU_OE.setFti(4);
        PRU_OE.setFtiRotw(4);
        PRU_OE.setReady(false);
    }

    @Test
    public void testVoidGameMapping() {
        Game vo = gameMapping.oeToVo(null, null);

        Assert.assertNull(vo);

        GameEntity entity = new GameEntity();

        vo = gameMapping.oeToVo(entity, null);

        ReflectionAssert.assertReflectionEquals(new Game(), vo);
    }

    @Test
    public void testFullGameMapping() {
        GameEntity entity = createGameEntity();

        Game vo = gameMapping.oeToVo(entity, null);

        ReflectionAssert.assertReflectionEquals(createGameVo(), vo);

        GameLight voLight = gameMapping.oeToVoLight(entity);

        ReflectionAssert.assertReflectionEquals(createGameVoLight(), voLight);
    }

    @Test
    public void testEconomicalSheetMapping() {
        List<EconomicalSheetCountry> vos = economicalSheetMapping.oesToVosCountry(createEconomicalSheetEntities());

        List<EconomicalSheet> expected = createEconomicalSheetVos();

        Assert.assertEquals(vos.size(), expected.size());
        for (int i = 0; i < vos.size() && i < expected.size(); i++) {
            ReflectionAssert.assertReflectionEquals(vos.get(i).getSheet(), expected.get(i));
            Assert.assertEquals(vos.get(i).getIdCountry(), FRA_OE.getId());
        }
    }

    private Game createGameVo() {
        Game object = new Game();

        object.setId(12L);
        object.setStatus(GameStatusEnum.ECONOMICAL_EVENT);
        object.setTurn(1);
        object.setVersion(15L);

        object.getCountries().add(FRA_VO);
        object.getCountries().add(PRU_VO);

        FRA_VO.setEconomicalSheets(createEconomicalSheetVos());
        FRA_VO.setAdministrativeActions(createAdministrativeActionsVos());
        FRA_VO.setMonarchs(createMonarchsVos());
        FRA_VO.setMonarch(createMonarchsVos().get(0));

        object.setEvents(createEventsVos());

        object.setRelations(createRelationsVos(object.getCountries().get(0), object.getCountries().get(0), object.getCountries().get(1)));

        object.setStacks(createStacksVos());

        return object;
    }

    private List<EconomicalSheet> createEconomicalSheetVos() {
        List<EconomicalSheet> objects = new ArrayList<>();

        EconomicalSheet object = new EconomicalSheet();
        object.setId(1L);
        object.setTurn(1);
        objects.add(object);

        object = new EconomicalSheet();
        object.setId(2L);
        object.setTurn(2);
        object.setActCampExpense(3);
        object.setWoodSlaves(4);
        object.setWealth(5);
        object.setVassalIncome(6);
        object.setUnitPurchExpense(7);
        object.setUnitMaintExpense(8);
        object.setTradeIncome(9);
        object.setTradeCenterLoss(10);
        object.setTradeCenterIncome(11);
        object.setTpIncome(12);
        object.setSubsidies(13);
        object.setStab(14);
        object.setSpecialIncome(15);
        object.setRtStart(16);
        object.setRtPeace(17);
        object.setRtEvents(18);
        object.setRtEnd(19);
        object.setActCampExpense(20);
        object.setAdminActExpense(21);
        object.setAdminReactExpense(22);
        object.setAdmTotalExpense(23);
        object.setColIncome(24);
        object.setDiploActions(25);
        object.setDiploReactions(26);
        object.setDomTradeIncome(27);
        object.setEventIncome(28);
        object.setExcRecruitExpense(29);
        object.setExcTaxes(30);
        object.setExcTaxesMod(31);
        object.setExoResIncome(32);
        object.setExpenses(33);
        object.setFleetLevelIncome(34);
        object.setFleetMonopIncome(35);
        object.setFortMaintExpense(36);
        object.setFortPurchExpense(37);
        object.setForTradeIncome(38);
        object.setGoldIncome(39);
        object.setGoldRotw(40);
        object.setGrossIncome(41);
        object.setIncome(42);
        object.setIndustrialIncome(43);
        object.setInflation(44);
        object.setInterBankrupt(45);
        object.setInterestExpense(46);
        object.setInterLoan(47);
        object.setInterLoanInterests(48);
        object.setInterLoanNew(49);
        object.setInterLoanRefund(50);
        object.setLandIncome(51);
        object.setLoans(52);
        object.setLostIncome(53);
        object.setMajCampExpense(54);
        object.setMandRefundExpense(55);
        object.setMaxInterLoan(56);
        object.setMaxNatLoan(57);
        object.setMilitaryExpense(58);
        object.setMissMaintExpense(59);
        object.setMnuIncome(60);
        object.setMultCampExpense(61);
        object.setNatLoan(62);
        object.setNatLoanBankrupt(63);
        object.setNatLoanEnd(64);
        object.setNatLoanInterest(65);
        object.setNatLoanNew(66);
        object.setNatLoanRefund(67);
        object.setNatLoanStart(68);
        object.setNavalRefitExpense(69);
        object.setOptRefundExpense(70);
        object.setOtherExpense(71);
        object.setPassCampExpense(72);
        object.setPeace(73);
        object.setPeriodWealth(74);
        object.setPillages(75);
        object.setPraesidioExpense(76);
        object.setPrestigeIncome(77);
        object.setPrestigeSpent(78);
        object.setPrestigeVP(79);
        object.setProvincesIncome(80);
        object.setRegularIncome(81);
        object.setRemainingExpenses(82);
        object.setRotwIncome(83);
        object.setRtAftExch(84);
        object.setRtBalance(85);
        object.setRtBefExch(86);
        object.setRtCollapse(87);
        object.setRtDiplo(88);
        objects.add(object);

        return objects;
    }

    private List<AdministrativeAction> createAdministrativeActionsVos() {
        List<AdministrativeAction> objects = new ArrayList<>();

        AdministrativeAction object = new AdministrativeAction();
        object.setId(1L);
        object.setTurn(2);
        object.setProvince("province");
        object.setBonus(6);
        object.setColumn(-2);
        object.setCost(30);
        object.setCounterFaceType(CounterFaceTypeEnum.ARMY_MINUS);
        object.setDie(9);
        object.setIdObject(12L);
        object.setResult(ResultEnum.AVERAGE_PLUS);
        object.setSecondaryDie(6);
        object.setSecondaryResult(false);
        object.setStatus(AdminActionStatusEnum.DONE);
        object.setType(AdminActionTypeEnum.COL);
        objects.add(object);

        return objects;
    }

    private List<PoliticalEvent> createEventsVos() {
        List<PoliticalEvent> objects = new ArrayList<>();

        PoliticalEvent object = new PoliticalEvent();
        object.setId(1L);
        object.setTurn(1);
        objects.add(object);
        object = new PoliticalEvent();
        object.setId(2L);
        object.setTurn(3);
        objects.add(object);
        object = new PoliticalEvent();
        object.setId(3L);
        object.setTurn(5);
        objects.add(object);

        return objects;
    }

    private List<Relation> createRelationsVos(PlayableCountry first, PlayableCountry second, PlayableCountry third) {
        List<Relation> objects = new ArrayList<>();

        Relation object = new Relation();
        object.setId(1L);
        object.setType(RelationTypeEnum.ALLIANCE);
        object.setFirst(first);
        object.setSecond(second);
        objects.add(object);
        object = new Relation();
        object.setId(2L);
        object.setType(RelationTypeEnum.WAR);
        object.setFirst(second);
        object.setSecond(third);
        objects.add(object);

        return objects;
    }

    private List<Stack> createStacksVos() {
        List<Stack> objects = new ArrayList<>();

        Stack object = new Stack();
        object.setId(1L);
        object.setProvince(PECS);
        object.setMovePhase(MovePhaseEnum.IS_MOVING);
        List<Counter> subObjects = new ArrayList<>();
        Counter subObject = new Counter();
        subObject.setId(1L);
        subObject.setType(CounterFaceTypeEnum.ARMY_MINUS);
        subObject.setCountry("FRA");
        subObject.setVeterans(2);
        subObject.setOwner(object);
        subObjects.add(subObject);
        subObject = new Counter();
        subObject.setId(2L);
        subObject.setType(CounterFaceTypeEnum.ARMY_PLUS);
        subObject.setCountry("PRU");
        subObject.setVeterans(0);
        subObject.setOwner(object);
        subObjects.add(subObject);
        object.setCounters(subObjects);
        objects.add(object);
        object = new Stack();
        object.setId(2L);
        object.setProvince(TYR);
        object.setMovePhase(MovePhaseEnum.MOVED);
        object.setBesieged(true);
        subObjects = new ArrayList<>();
        subObject = new Counter();
        subObject.setId(3L);
        subObject.setType(CounterFaceTypeEnum.MNU_ART_MINUS);
        subObject.setCountry("FRA");
        subObject.setOwner(object);
        subObjects.add(subObject);
        object.setCounters(subObjects);
        objects.add(object);
        object = new Stack();
        object.setId(3L);
        object.setProvince(IDF);
        object.setMovePhase(MovePhaseEnum.NOT_MOVED);
        object.setBesieged(false);
        objects.add(object);

        return objects;
    }

    private List<Monarch> createMonarchsVos() {
        List<Monarch> objects = new ArrayList<>();

        Monarch object = new Monarch();
        object.setId(1L);
        object.setBegin(1);
        object.setEnd(12);
        object.setAdministrative(9);
        object.setDiplomacy(9);
        object.setMilitary(9);
        object.setMilitaryAverage(5);
        objects.add(object);

        object = new Monarch();
        object.setId(2L);
        object.setBegin(13);
        object.setEnd(63);
        object.setAdministrative(3);
        object.setDiplomacy(3);
        object.setMilitary(3);
        object.setMilitaryAverage(0);
        objects.add(object);

        return objects;
    }

    private GameLight createGameVoLight() {
        GameLight game = new GameLight();

        game.setId(12L);
        game.setStatus(GameStatusEnum.ECONOMICAL_EVENT);
        game.setTurn(1);

        return game;
    }

    private GameEntity createGameEntity() {
        GameEntity object = new GameEntity();

        object.setId(12L);
        object.setStatus(GameStatusEnum.ECONOMICAL_EVENT);
        object.setTurn(1);
        object.setVersion(15L);

        object.getCountries().add(FRA_OE);
        object.getCountries().add(PRU_OE);

        FRA_OE.setEconomicalSheets(createEconomicalSheetEntities());
        FRA_OE.setAdministrativeActions(createAdministrativeActionsEntities());
        FRA_OE.setMonarchs(createMonarchsEntities());
        FRA_OE.setMonarch(createMonarchsEntities().get(0));

        object.setEvents(createEventsEntities());


        object.setRelations(createRelationsEntities(object.getCountries().get(0), object.getCountries().get(0), object.getCountries().get(1)));

        object.setStacks(createStacksEntities());

        return object;
    }

    private List<EconomicalSheetEntity> createEconomicalSheetEntities() {
        List<EconomicalSheetEntity> objects = new ArrayList<>();

        EconomicalSheetEntity object = new EconomicalSheetEntity();
        object.setId(1L);
        object.setCountry(FRA_OE);
        object.setTurn(1);
        objects.add(object);

        object = new EconomicalSheetEntity();
        object.setId(2L);
        object.setCountry(FRA_OE);
        object.setTurn(2);
        object.setActCampExpense(3);
        object.setWoodSlaves(4);
        object.setWealth(5);
        object.setVassalIncome(6);
        object.setUnitPurchExpense(7);
        object.setUnitMaintExpense(8);
        object.setTradeIncome(9);
        object.setTradeCenterLoss(10);
        object.setTradeCenterIncome(11);
        object.setTpIncome(12);
        object.setSubsidies(13);
        object.setStab(14);
        object.setSpecialIncome(15);
        object.setRtStart(16);
        object.setRtPeace(17);
        object.setRtEvents(18);
        object.setRtEnd(19);
        object.setActCampExpense(20);
        object.setAdminActExpense(21);
        object.setAdminReactExpense(22);
        object.setAdmTotalExpense(23);
        object.setColIncome(24);
        object.setDiploActions(25);
        object.setDiploReactions(26);
        object.setDomTradeIncome(27);
        object.setEventIncome(28);
        object.setExcRecruitExpense(29);
        object.setExcTaxes(30);
        object.setExcTaxesMod(31);
        object.setExoResIncome(32);
        object.setExpenses(33);
        object.setFleetLevelIncome(34);
        object.setFleetMonopIncome(35);
        object.setFortMaintExpense(36);
        object.setFortPurchExpense(37);
        object.setForTradeIncome(38);
        object.setGoldIncome(39);
        object.setGoldRotw(40);
        object.setGrossIncome(41);
        object.setIncome(42);
        object.setIndustrialIncome(43);
        object.setInflation(44);
        object.setInterBankrupt(45);
        object.setInterestExpense(46);
        object.setInterLoan(47);
        object.setInterLoanInterests(48);
        object.setInterLoanNew(49);
        object.setInterLoanRefund(50);
        object.setLandIncome(51);
        object.setLoans(52);
        object.setLostIncome(53);
        object.setMajCampExpense(54);
        object.setMandRefundExpense(55);
        object.setMaxInterLoan(56);
        object.setMaxNatLoan(57);
        object.setMilitaryExpense(58);
        object.setMissMaintExpense(59);
        object.setMnuIncome(60);
        object.setMultCampExpense(61);
        object.setNatLoan(62);
        object.setNatLoanBankrupt(63);
        object.setNatLoanEnd(64);
        object.setNatLoanInterest(65);
        object.setNatLoanNew(66);
        object.setNatLoanRefund(67);
        object.setNatLoanStart(68);
        object.setNavalRefitExpense(69);
        object.setOptRefundExpense(70);
        object.setOtherExpense(71);
        object.setPassCampExpense(72);
        object.setPeace(73);
        object.setPeriodWealth(74);
        object.setPillages(75);
        object.setPraesidioExpense(76);
        object.setPrestigeIncome(77);
        object.setPrestigeSpent(78);
        object.setPrestigeVP(79);
        object.setProvincesIncome(80);
        object.setRegularIncome(81);
        object.setRemainingExpenses(82);
        object.setRotwIncome(83);
        object.setRtAftExch(84);
        object.setRtBalance(85);
        object.setRtBefExch(86);
        object.setRtCollapse(87);
        object.setRtDiplo(88);
        objects.add(object);

        return objects;
    }

    private List<AdministrativeActionEntity> createAdministrativeActionsEntities() {
        List<AdministrativeActionEntity> objects = new ArrayList<>();

        AdministrativeActionEntity object = new AdministrativeActionEntity();
        object.setId(1L);
        object.setTurn(2);
        object.setProvince("province");
        object.setBonus(6);
        object.setColumn(-2);
        object.setCost(30);
        object.setCounterFaceType(CounterFaceTypeEnum.ARMY_MINUS);
        object.setDie(9);
        object.setIdObject(12L);
        object.setResult(ResultEnum.AVERAGE_PLUS);
        object.setSecondaryDie(6);
        object.setSecondaryResult(false);
        object.setStatus(AdminActionStatusEnum.DONE);
        object.setType(AdminActionTypeEnum.COL);
        objects.add(object);

        return objects;
    }

    private List<PoliticalEventEntity> createEventsEntities() {
        List<PoliticalEventEntity> objects = new ArrayList<>();

        PoliticalEventEntity object = new PoliticalEventEntity();
        object.setId(1L);
        object.setTurn(1);
        objects.add(object);
        object = new PoliticalEventEntity();
        object.setId(2L);
        object.setTurn(3);
        objects.add(object);
        object = new PoliticalEventEntity();
        object.setId(3L);
        object.setTurn(5);
        objects.add(object);

        return objects;
    }

    private List<RelationEntity> createRelationsEntities(PlayableCountryEntity first, PlayableCountryEntity second, PlayableCountryEntity third) {
        List<RelationEntity> objects = new ArrayList<>();

        RelationEntity object = new RelationEntity();
        object.setId(1L);
        object.setType(RelationTypeEnum.ALLIANCE);
        object.setFirst(first);
        object.setSecond(second);
        objects.add(object);
        object = new RelationEntity();
        object.setId(2L);
        object.setType(RelationTypeEnum.WAR);
        object.setFirst(second);
        object.setSecond(third);
        objects.add(object);

        return objects;
    }

    private List<StackEntity> createStacksEntities() {
        List<StackEntity> objects = new ArrayList<>();

        StackEntity object = new StackEntity();
        object.setId(1L);
        object.setProvince(PECS);
        object.setMovePhase(MovePhaseEnum.IS_MOVING);
        List<CounterEntity> subObjects = new ArrayList<>();
        CounterEntity subObject = new CounterEntity();
        subObject.setId(1L);
        subObject.setType(CounterFaceTypeEnum.ARMY_MINUS);
        subObject.setCountry("FRA");
        subObject.setVeterans(2);
        subObject.setOwner(object);
        subObjects.add(subObject);
        subObject = new CounterEntity();
        subObject.setId(2L);
        subObject.setType(CounterFaceTypeEnum.ARMY_PLUS);
        subObject.setCountry("PRU");
        subObject.setVeterans(0);
        subObject.setOwner(object);
        subObjects.add(subObject);
        object.setCounters(subObjects);
        objects.add(object);
        object = new StackEntity();
        object.setId(2L);
        object.setProvince(TYR);
        object.setMovePhase(MovePhaseEnum.MOVED);
        object.setBesieged(true);
        subObjects = new ArrayList<>();
        subObject = new CounterEntity();
        subObject.setId(3L);
        subObject.setType(CounterFaceTypeEnum.MNU_ART_MINUS);
        subObject.setCountry("FRA");
        subObject.setOwner(object);
        subObjects.add(subObject);
        object.setCounters(subObjects);
        objects.add(object);
        object = new StackEntity();
        object.setId(3L);
        object.setProvince(IDF);
        object.setMovePhase(MovePhaseEnum.NOT_MOVED);
        object.setBesieged(false);
        objects.add(object);

        return objects;
    }

    private List<MonarchEntity> createMonarchsEntities() {
        List<MonarchEntity> objects = new ArrayList<>();

        MonarchEntity object = new MonarchEntity();
        object.setId(1L);
        object.setBegin(1);
        object.setEnd(12);
        object.setAdministrative(9);
        object.setDiplomacy(9);
        object.setMilitary(9);
        object.setMilitaryAverage(5);
        objects.add(object);

        object = new MonarchEntity();
        object.setId(2L);
        object.setBegin(13);
        object.setEnd(63);
        object.setAdministrative(3);
        object.setDiplomacy(3);
        object.setMilitary(3);
        object.setMilitaryAverage(0);
        objects.add(object);

        return objects;
    }
}
