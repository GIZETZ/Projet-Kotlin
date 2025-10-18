# Design system et grille d’espacement

Ce projet utilise Material 3 avec une charte énergique (orange mandarine, ambre, turquoise) et un thème clair/sombre. Les couleurs sont centralisées dans `app/src/main/res/values/colors.xml` et `app/src/main/res/values-night/colors.xml`. La typographie par défaut est Inter (Downloadable Fonts), et les composants ont des coins arrondis de 12dp via `ShapeAppearance`.

## Grille d’espacement (dimens)
Les espacements sont définis dans `app/src/main/res/values/dimens.xml` et doivent être utilisés à la place des valeurs brutes en `dp`.

- `@dimen/space_4`  → 4dp
- `@dimen/space_8`  → 8dp
- `@dimen/space_12` → 12dp
- `@dimen/space_16` → 16dp
- `@dimen/space_20` → 20dp
- `@dimen/space_24` → 24dp

Exemples d’usage:

```xml
<!-- Marges/paddings -->
android:padding="@dimen/space_16"
android:layout_marginTop="@dimen/space_8"

<!-- Cartes -->
app:cardCornerRadius="12dp"  <!-- coins unifiés -->
```

## Bonnes pratiques UI
- Couleurs: ne pas utiliser de couleurs codées en dur dans les layouts. Utiliser les attributs de thème:
  - `?attr/colorPrimary`, `?attr/colorOnPrimary`
  - `?attr/colorSecondary`, `?attr/colorOnSecondary`
  - `?attr/colorBackground`, `?attr/colorOnBackground`
  - `?attr/colorOnSurfaceVariant` pour le texte secondaire
- Typo: ne pas redéfinir `fontFamily` localement sauf cas particulier (titres spéciaux). Le thème fournit Inter globalement.
- Composants: utiliser les variantes Material3 (`MaterialToolbar`, `MaterialButton`, `TextInputLayout`, `MaterialCardView`, `Chip`, `LinearProgressIndicator`) sans overrides de couleurs locaux.
- Sombre/Clair: éviter de référencer `@color/...` directement dans les layouts; préférez les `?attr/...` pour bénéficier des deux modes.

## Où modifier
- Thème: `app/src/main/res/values/themes.xml`
- Couleurs: `app/src/main/res/values/colors.xml`, `app/src/main/res/values-night/colors.xml`
- Espacements: `app/src/main/res/values/dimens.xml`
- Police Inter: `app/src/main/res/font/inter.xml` (+ `values/fonts_certs.xml`)

