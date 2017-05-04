/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ScoreType implements Serializable {

    private static final long serialVersionUID = 992731811;

    private Integer scoreTypeId;
    private String  scoreTypeName;

    public ScoreType() {}

    public ScoreType(ScoreType value) {
        this.scoreTypeId = value.scoreTypeId;
        this.scoreTypeName = value.scoreTypeName;
    }

    public ScoreType(
        Integer scoreTypeId,
        String  scoreTypeName
    ) {
        this.scoreTypeId = scoreTypeId;
        this.scoreTypeName = scoreTypeName;
    }

    @NotNull
    public Integer getScoreTypeId() {
        return this.scoreTypeId;
    }

    public void setScoreTypeId(Integer scoreTypeId) {
        this.scoreTypeId = scoreTypeId;
    }

    @NotNull
    @Size(max = 20)
    public String getScoreTypeName() {
        return this.scoreTypeName;
    }

    public void setScoreTypeName(String scoreTypeName) {
        this.scoreTypeName = scoreTypeName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ScoreType (");

        sb.append(scoreTypeId);
        sb.append(", ").append(scoreTypeName);

        sb.append(")");
        return sb.toString();
    }
}
