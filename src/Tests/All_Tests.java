package Tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Tests.Environment.Board_Test.class,             
        Tests.Game.Game_Test.class,       
        Tests.Objects.Player_Test.class,
        Tests.Objects.Room_Test.class,
       
})
public class All_Tests {

}
