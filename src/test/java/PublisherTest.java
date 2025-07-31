
import com.notify.publisher.PublisherType;
import com.notify.publisher.UserPublisher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PublisherTest {

    @Test
    void testUserPublisherAttributes() {
        UserPublisher publisher = new UserPublisher("Admin");

        assertEquals("Admin", publisher.getName());
        assertEquals(PublisherType.USER, publisher.getType());

    }
}
