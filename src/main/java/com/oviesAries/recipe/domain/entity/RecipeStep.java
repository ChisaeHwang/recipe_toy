package com.oviesAries.recipe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RECIPE_STEP")
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    private Integer stepOrder;

    private String description;

    private String imagePath;

    @Version
    private Long version;

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        if (!recipe.getSteps().contains(this)) {
            recipe.getSteps().add(this);
        }
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }
}
