import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

public class PECMAC125A
{
	public static void main(String args[]) throws Exception
	{
		// Get I2C bus
		I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
		// Get I2C device, PECMAC1256A I2C address is 0x2A(42)
		I2CDevice device = bus.getDevice(0x2A);
		
		// Read configuration command
		byte[] readConfigCommand = {(byte)0x92, (byte)0x6A, 0x02, 0x00, 0x00, 0x00, 0x00, (byte)0xFE};
		
		// Send read configuration command
		device.write(readConfigCommand, 0, 8);
		Thread.sleep(300);
		
		// Read 3 bytes of data
		// TypeOfSensor, maxCurrent, noOfChannel
		byte[] data = new byte[36];
		device.read(data, 0, 3);
		
		int typeOfSensor = data[0];
		int maxCurrent = data[1];
		int noOfChannel = data[2];
		
		// Output data to screen
		System.out.printf("Type Of Sensor : %d%n", typeOfSensor);
		System.out.printf("Max Current : %d A%n", maxCurrent);
		System.out.printf("No Of Channel : %d%n", noOfChannel);
		
		// Read current command, Channel 1
		byte[] readCurrentCommand = {(byte)0x92, (byte)0x6A, 0x01, 0x01, 0x0C, 0x00, 0x00, (byte)0x0A};
		
		// Send read current command
		device.write(readCurrentCommand, 0, 8);
		Thread.sleep(300);
		
        // Read Current data
		device.read(data, 0, noOfChannel*3);
        Thread.sleep(500);
        
        for(int i = 0; i < noOfChannel; i++)
        {
            // Msb1, msb, lsb
            int msb1 = data[i*3] & 0xff;
            int msb = data[1+i*3] & 0xff ;
            int lsb = data[2+i*3] & 0xff;
         System.out.println(msb1+ " " + msb + " "+lsb); 
            float current = (msb1 * 65536) + (msb * 256) + lsb;
            current = current / 1000;
            
            System.out.printf("Channel : %d %n", i+1);
            System.out.printf("Current : %.3f A%n", current);
        }
    }
}

