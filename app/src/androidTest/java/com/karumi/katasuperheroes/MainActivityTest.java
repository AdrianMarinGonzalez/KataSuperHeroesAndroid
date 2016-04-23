/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

//    @Test public void testEmptyCaseNotShowingWhenThereAreSuperheroes( int superheroesNumber, boolean isAvenger ){
//        givenThereAreSomeSuperHeroes( 10 );
//
//        startActivity();
//
//        onView( withText( "¯\\_(ツ)_/¯" ) ).check( matches( not(isDisplayed()) ) );
//    }

    @Test public void testNumberOfSuperheroesShown() {
        int totalSuperheroes = 10;

        givenThereAreSomeSuperHeroes( totalSuperheroes );

        startActivity();

        onView( withId( R.id.recycler_view ) ).check( matches( RecyclerViewItemsCountMatcher.recyclerViewHasItemCount( totalSuperheroes ) ) );
    }

    @Test public void showsSuperHeroesName() {
        int number = 1000;

        givenThereAreSomeSuperHeroes( number );

        startActivity();

        for ( int i = 0; i < number; i++ ) {
            onView( withText( "SuperHeroe " + i ) ).check( matches( isDisplayed() ) );
            if ( repository.getAll().get( i ).isAvenger() ) {
                onView( allOf( withId(R.id.iv_avengers_badge), hasSibling( withText( "SuperHeroe " + i ) ) ) )
                        .check(matches(isDisplayed()));
            }

            onView( withId( R.id.recycler_view ) ).perform( RecyclerViewActions.scrollToPosition( i ) );
        }
    }

  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

    private void givenThereAreSomeSuperHeroes(int number) {
        List<SuperHero> list = new ArrayList<>(  );
        for ( int i = 0; i < number; i++ ) {
            final boolean isAvenger = i%2==0;
            list.add( new SuperHero( "SuperHeroe " + i, "http://img.lum.dolimg.com/v1/images/3f1baa0812f35ac2910feeaf463a8d9c437a9b19.png?region=0,0,600,600", isAvenger , "Superheroe number " + i ) );
        }
        when( repository.getAll() ).thenReturn( list );
    }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}