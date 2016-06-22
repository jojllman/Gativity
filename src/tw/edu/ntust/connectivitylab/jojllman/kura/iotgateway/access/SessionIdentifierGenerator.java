package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import java.security.SecureRandom;
import java.math.BigInteger;

public final class SessionIdentifierGenerator {
  private SecureRandom random = new SecureRandom();

  public String nextSessionId() {
    return new BigInteger(130, random).toString(32);
  }
}
