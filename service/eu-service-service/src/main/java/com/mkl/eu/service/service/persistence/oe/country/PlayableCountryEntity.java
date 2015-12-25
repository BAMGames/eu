package com.mkl.eu.service.service.persistence.oe.country;

import com.mkl.eu.service.service.persistence.oe.GameEntity;
import com.mkl.eu.service.service.persistence.oe.IEntity;
import com.mkl.eu.service.service.persistence.oe.eco.EconomicalSheetEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Country (major or former major or future major one).
 *
 * @author MKL
 */
@Entity
@Table(name = "COUNTRY")
public class PlayableCountryEntity implements IEntity, Serializable {
    /** Id. */
    private Long id;
    /**
     * Name of the country.
     */
    private String name;
    /** Name of the player. External functional id. */
    private String username;
    /** DTI of the country. */
    private int dti;
    /** Max DTI of the country. */
    private int dtiMax;
    /** FTI of the country. */
    private int fti;
    /** Max FTI of the country. */
    private int ftiMax;
    /**
     * Economical sheet by turn of the country.
     */
    private List<EconomicalSheetEntity> economicalSheets;
    /** Game of the entity. */
    private GameEntity game;

    /** @return the id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    /** @param id the id to set. */

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name.
     */
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the username. */
    @Column(name = "USERNAME")
    public String getUsername() {
        return username;
    }

    /** @param username the username to set. */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the dti. */
    @Column(name = "DTI")
    public int getDti() {
        return dti;
    }

    /** @param dti the dti to set. */
    public void setDti(int dti) {
        this.dti = dti;
    }

    /** @return the dtiMax. */
    @Column(name = "DTI_MAX")
    public int getDtiMax() {
        return dtiMax;
    }

    /** @param dtiMax the dtiMax to set. */
    public void setDtiMax(int dtiMax) {
        this.dtiMax = dtiMax;
    }

    /** @return the fti. */
    @Column(name = "FTI")
    public int getFti() {
        return fti;
    }

    /** @param fti the fti to set. */
    public void setFti(int fti) {
        this.fti = fti;
    }

    /** @return the ftiMax. */
    @Column(name = "FTI_MAX")
    public int getFtiMax() {
        return ftiMax;
    }

    /** @param ftiMax the ftiMax to set. */
    public void setFtiMax(int ftiMax) {
        this.ftiMax = ftiMax;
    }

    /** @return the economicalSheets. */
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<EconomicalSheetEntity> getEconomicalSheets() {
        return economicalSheets;
    }

    /** @param economicalSheets the economicalSheets to set. */
    public void setEconomicalSheets(List<EconomicalSheetEntity> economicalSheets) {
        this.economicalSheets = economicalSheets;
    }

    /** @return the game. */
    @ManyToOne
    @JoinColumn(name = "ID_GAME")
    public GameEntity getGame() {
        return game;
    }

    /** @param game the game to set. */
    public void setGame(GameEntity game) {
        this.game = game;
    }
}
