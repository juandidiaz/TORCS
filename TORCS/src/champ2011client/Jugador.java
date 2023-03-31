package champ2011client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import QLearning.Datos;
import QLearning.QTable;
import champ2011client.Action;
import champ2011client.Controller;
import champ2011client.SensorModel;
import champ2011client.SocketHandler;
import champ2011client.Controller.Stage;

public class Jugador extends Controller{
	
	private static QTable qtabla=new QTable();
	
		SocketHandler socket;
		/* Gear Changing Constants */
		final int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
		final int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };

		/* Stuck constants */
		final int stuckTime = 25;
		final float stuckAngle = (float) 0.523598775; // PI/6

		/* Accel and Brake Constants */
		final float maxSpeedDist = 7;
		final float maxSpeed = 50;
		final float sin5 = (float) 0.08716;
		final float cos5 = (float) 0.99619;

		/* Steering constants */
		final float steerLock = (float) 0.785398;
		final float steerSensitivityOffset = (float) 80.0;
		final float wheelSensitivityCoeff = 1;

		/* ABS Filter Constants */
		final float wheelRadius[] = { (float) 0.3179, (float) 0.3179, (float) 0.3276, (float) 0.3276 };
		final float absSlip = (float) 2.0;
		final float absRange = (float) 3.0;
		final float absMinSpeed = (float) 3.0;

		/* Clutching Constants */
		final float clutchMax = (float) 0.5;
		final float clutchDelta = (float) 0.05;
		final float clutchRange = (float) 0.82;
		final float clutchDeltaTime = (float) 0.02;
		final float clutchDeltaRaced = 10;
		final float clutchDec = (float) 0.01;
		final float clutchMaxModifier = (float) 1.3;
		final float clutchMaxTime = (float) 1.5;
		private int stuck=0;
		private float clutch=0;
		
	public Jugador() {
		qtabla.CargarQTable();
	}
	@Override
	public Action control(SensorModel sensors,SocketHandler socket) {
		// check if car is currently stuck
		if ( Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle )
	    {
			// update stuck counter
	        stuck++;
	    }
	    else
	    {
	    	// if not stuck reset stuck counter
	        stuck = 0;
	    }

		// after car is stuck for a while apply recovering policy
	    if (stuck > stuckTime)
	    {
	    	/* set gear and sterring command assuming car is 
	    	 * pointing in a direction out of track */
	    	
	    	// to bring car parallel to track axis
	        float steer = (float) (- sensors.getAngleToTrackAxis() / steerLock); 
	        int gear=-1; // gear R
	        
	        // if car is pointing in the correct direction revert gear and steer  
	        if (sensors.getAngleToTrackAxis()*sensors.getTrackPosition()>0)
	        {
	            gear = 1;
	            steer = -steer;
	        }
	        clutch = clutching(sensors, clutch);
	        // build a CarControl variable and return it
	        Action action = new Action ();
	        action.gear = gear;
	        action.steering = steer;
	        action.accelerate = 1.0;
	        action.brake = 0;
	        action.clutch = clutch;
	        return action;
	    }
	    else {
		// compute accel/brake command
				float accel_and_brake = getAccel(sensors);
				// compute gear
				int gear = getGear(sensors);

				float steer;
				// compute steering
				// float steer = getSteer(sensors);
			

//			        // set accel and brake from the joint accel/brake command 
				float accel, brake;
				if (accel_and_brake > 0) {
					accel = accel_and_brake;
					brake = 0;
				} else {
					accel = 0;
					// apply ABS to brake
					brake = filterABS(sensors, -accel_and_brake);
				}
//			        
				clutch = clutching(sensors, clutch);

//			        
				// build a CarControl variable and return it
				Action action = new Action();
				int estado=this.getEstadoActual(sensors);
				int movimiento=qtabla.obtenerMejorMovimiento(estado);
				action.gear = gear;
				steer=Datos.direccion[movimiento];
				action.steering = steer;
				action.accelerate = accel;
				action.brake = 0;
				action.clutch = 0;
				return action;
	    }
	}
	public Integer getEstadoActual(SensorModel sensors)
	{
		
		double PosicionPista = sensors.getTrackPosition();
		double angulo = sensors.getAngleToTrackAxis();
		
		if(PosicionPista==0)
		{
			if(angulo==0)
			{
				return 0;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 1;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 2;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 3;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 4;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 5;
			}
			else if(angulo>=1.570796)
			{
				return 6;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 7;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 8;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 9;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 10;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 11;
			}
			else if(angulo<=-1.570796)
			{
				return 12;
			}

				
		}
		//Izquierda
		else if(PosicionPista>0 && PosicionPista <0.1)
		{
			if(angulo==0)
			{
				return 13;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 14;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 15;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 16;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 17;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 18;
			}
			else if(angulo>=1.570796)
			{
				return 19;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 20;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 21;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 22;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 23;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 24;
			}
			else if(angulo<=-1.570796)
			{
				return 25;
			}

		}
		else if(PosicionPista>=0.1 && PosicionPista<0.2)
		{
			if(angulo==0)
			{
				return 26;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 27;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 28;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 29;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 30;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 31;
			}
			else if(angulo>=1.570796)
			{
				return 32;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 33;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 34;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 35;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 36;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 37;
			}
			else if(angulo<=-1.570796)
			{
				return 38;
			}

		}
		else if(PosicionPista>=0.2 && PosicionPista<0.3)
		{
			if(angulo==0)
			{
				return 39;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 40;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 41;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 42;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 43;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 44;
			}
			else if(angulo>=1.570796)
			{
				return 45;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 46;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 47;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 48;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 49;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 50;
			}
			else if(angulo<=-1.570796)
			{
				return 51;
			}

		}
		else if(PosicionPista>=0.3 && PosicionPista<0.4)
		{
			if(angulo==0)
			{
				return 52;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 53;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 54;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 55;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 56;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 57;
			}
			else if(angulo>=1.570796)
			{
				return 58;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 59;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 60;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 61;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 62;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 63;
			}
			else if(angulo<=-1.570796)
			{
				return 64;
			}

		}
		else if(PosicionPista>=0.4 && PosicionPista<0.5)
		{
			if(angulo==0)
			{
				return 65;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 66;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 67;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 68;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 69;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 70;
			}
			else if(angulo>=1.570796)
			{
				return 71;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 72;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 73;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 74;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 75;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 76;
			}
			else if(angulo<=-1.570796)
			{
				return 77;
			}

		}
		else if(PosicionPista>=0.5 && PosicionPista<0.6)
		{
			if(angulo==0)
			{
				return 78;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 79;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 80;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 81;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 82;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 83;
			}
			else if(angulo>=1.570796)
			{
				return 84;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 85;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 86;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 87;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 88;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 89;
			}
			else if(angulo<=-1.570796)
			{
				return 90;
			}

		}
		else if(PosicionPista>=0.6 && PosicionPista<0.7)
		{
			if(angulo==0)
			{
				return 91;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 92;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 93;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 94;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 95;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 96;
			}
			else if(angulo>=1.570796)
			{
				return 97;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 98;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 99;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 100;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 101;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 102;
			}
			else if(angulo<=-1.570796)
			{
				return 103;
			}

		}
		else if(PosicionPista>=0.7 && PosicionPista<0.8)
		{
			if(angulo==0)
			{
				return 104 ;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 105;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 106;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 107;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 108;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 109;
			}
			else if(angulo>=1.570796)
			{
				return 110;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 111;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 112;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 113;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 114;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 115;
			}
			else if(angulo<=-1.570796)
			{
				return 116;
			}

		}
		else if(PosicionPista>=0.8 && PosicionPista<0.9)
		{
			if(angulo==0)
			{
				return 117;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 118;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 119;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 120;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 121;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 122;
			}
			else if(angulo>=1.570796)
			{
				return 123;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 124;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 125;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 126;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 127;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 128;
			}
			else if(angulo<=-1.570796)
			{
				return 129;
			}

		}
		else if(PosicionPista>=0.9)
		{
			if(angulo==0)
			{
				return 130;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 131;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 132;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 133;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 134;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 135;
			}
			else if(angulo>=1.570796)
			{
				return 136;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 137;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 138;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 139;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 140;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 141;
			}
			else if(angulo<=-1.570796)
			{
				return 142;
			}

		}
		else if(PosicionPista<0 && PosicionPista>-0.1)
		{
			if(angulo==0)
			{
				return 143;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 144;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 145;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 146;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 147;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 148;
			}
			else if(angulo>=1.570796)
			{
				return 149;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 150;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 151;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 152;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 153;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 154;
			}
			else if(angulo<=-1.570796)
			{
				return 155;
			}

		}
		else if(PosicionPista<=-0.1 && PosicionPista>-0.2)
		{
			if(angulo==0)
			{
				return 156;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 157;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 158;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 159;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 160;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 161;
			}
			else if(angulo>=1.570796)
			{
				return 162;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 163;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 164;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 165;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 166;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 167;
			}
			else if(angulo<=-1.570796)
			{
				return 168;
			}

		}
		else if(PosicionPista<=-0.2 && PosicionPista>-0.3) {
			if(angulo==0)
			{
				return 169;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 170;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 171;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 172;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 173;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 174;
			}
			else if(angulo>=1.570796)
			{
				return 175;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 176;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 177;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 178;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 179;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 180;
			}
			else if(angulo<=-1.570796)
			{
				return 181;
			}

		}
		else if(PosicionPista<=-0.3 && PosicionPista >-0.4)
		{
			if(angulo==0)
			{
				return 182;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 183;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 184;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 185;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 186;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 187;
			}
			else if(angulo>=1.570796)
			{
				return 188;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 189;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 190;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 191;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 192;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 193;
			}
			else if(angulo<=-1.570796)
			{
				return 194;
			}

		}
		else if(PosicionPista<=-0.4 && PosicionPista>-0.5)
		{
			if(angulo==0)
			{
				return 195;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 196;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 197;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 198;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 199;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 200;
			}
			else if(angulo>=1.570796)
			{
				return 201;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 202;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 203;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 204;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 205;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 206;
			}
			else if(angulo<=-1.570796)
			{
				return 207;
			}

		}
		else if(PosicionPista<=-0.5 && PosicionPista>-0.6)
		{
			if(angulo==0)
			{
				return 208;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 209;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 210;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 211;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 212;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 213;
			}
			else if(angulo>=1.570796)
			{
				return 214;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 215;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 216;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 217;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 218;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 219;
			}
			else if(angulo<=-1.570796)
			{
				return 220;
			}

		}
		else if(PosicionPista<=-0.6 && PosicionPista>-0.7)
		{
			if(angulo==0)
			{
				return 221;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 222;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 223;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 224;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 225;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 226;
			}
			else if(angulo>=1.570796)
			{
				return 227;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 228;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 229;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 230;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 231;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 232;
			}
			else if(angulo<=-1.570796)
			{
				return 233;
			}

		}
		else if(PosicionPista<=-0.7 && PosicionPista>-0.8)
		{
			if(angulo==0)
			{
				return 234;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 235;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 236;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 237;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 238;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 239;
			}
			else if(angulo>=1.570796)
			{
				return 240;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 241;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 242;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 243;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 244;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 245;
			}
			else if(angulo<=-1.570796)
			{
				return 246;
			}

		}
		else if(PosicionPista<=-0.8 && PosicionPista>-0.9)
		{
			if(angulo==0)
			{
				return 247;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 248;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 249;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 250;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 251;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 252;
			}
			else if(angulo>=1.570796)
			{
				return 253;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 254;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 255;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 256;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 257;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 258;
			}
			else if(angulo<=-1.570796)
			{
				return 259;
			}

		}
		else if(PosicionPista<=-0.9)
		{
			if(angulo==0)
			{
				return 260;
			}
			else if(angulo>0 && angulo<0.314159)
			{
				return 261;
			}
			else if(angulo>=0.314159 && angulo<0.628318)
			{
				return 262;
			}
			else if(angulo>=0.628318 && angulo<0.942477)
			{
				return 263;
			}
			else if(angulo>=0.942477 && angulo<1.256637)
			{
				return 264;
			}
			else if(angulo>=1.256637 && angulo<1.570796)
			{
				return 265;
			}
			else if(angulo>=1.570796)
			{
				return 266;
			}
			else if(angulo<0 && angulo>-0.314159)
			{
				return 267;
			}
			else if(angulo<=-0.314159 && angulo>-0.628318)
			{
				return 268;
			}
			else if(angulo<=-0.628318 && angulo>-0.942477)
			{
				return 269;
			}
			else if(angulo<=-0.942477 && angulo>-1.256637)
			{
				return 270;
			}
			else if(angulo<=-1.256637 && angulo>-1.570796)
			{
				return 271;
			}
			else if(angulo<=-1.570796)
			{
				return 272;
			}

		}

		return null;
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public float[] initAngles()	{
		float[] angles = new float[19];

		/* set angles as {-90,-75,-60,-45,-30,-20,-15,-10,-5,0,5,10,15,20,30,45,60,75,90} */
		for (int i=0; i<5; i++)
		{
			angles[i]=-90+i*15;
			angles[18-i]=90-i*15;
		}

		for (int i=5; i<9; i++)
		{
				angles[i]=-20+(i-5)*5;
				angles[18-i]=20-(i-5)*5;
		}
		angles[9]=0;
		return angles;
	}
	

	private float filterABS(SensorModel sensors, float brake) {
		// convert speed to m/s
		float speed = (float) (sensors.getSpeed() / 3.6);
		// when spedd lower than min speed for abs do nothing
		if (speed < absMinSpeed)
			return brake;

		// compute the speed of wheels in m/s
		float slip = 0.0f;
		for (int i = 0; i < 4; i++) {
			slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
		}
		// slip is the difference between actual speed of car and average speed of
		// wheels
		slip = speed - slip / 4.0f;
		// when slip too high applu ABS
		if (slip > absSlip) {
			brake = brake - (slip - absSlip) / absRange;
		}

		// check brake is not negative, otherwise set it to zero
		if (brake < 0)
			return 0;
		else
			return brake;
	}

	float clutching(SensorModel sensors, float clutch) {

		float maxClutch = clutchMax;

		// Check if the current situation is the race start
		if (sensors.getCurrentLapTime() < clutchDeltaTime && getStage() == Stage.RACE
				&& sensors.getDistanceRaced() < clutchDeltaRaced)
			clutch = maxClutch;

		// Adjust the current value of the clutch
		if (clutch > 0) {
			double delta = clutchDelta;
			if (sensors.getGear() < 2) {
				// Apply a stronger clutch output when the gear is one and the race is just
				// started
				delta /= 2;
				maxClutch *= clutchMaxModifier;
				if (sensors.getCurrentLapTime() < clutchMaxTime)
					clutch = maxClutch;
			}

			// check clutch is not bigger than maximum values
			clutch = Math.min(maxClutch, clutch);

			// if clutch is not at max value decrease it quite quickly
			if (clutch != maxClutch) {
				clutch -= delta;
				clutch = Math.max((float) 0.0, clutch);
			}
			// if clutch is at max value decrease it very slowly
			else
				clutch -= clutchDec;
		}
		return clutch;
	}
	
	private int getGear(SensorModel sensors) {
		int gear = sensors.getGear();
		double rpm = sensors.getRPM();

		// if gear is 0 (N) or -1 (R) just return 1
		if (gear < 1)
			return 1;
		// check if the RPM value of car is greater than the one suggested
		// to shift up the gear from the current one
		if (gear < 6 && rpm >= gearUp[gear - 1])
			return gear + 1;
		else
		// check if the RPM value of car is lower than the one suggested
		// to shift down the gear from the current one
		if (gear > 1 && rpm <= gearDown[gear - 1])
			return gear - 1;
		else // otherwhise keep current gear
			return gear;
	}


	private float getAccel(SensorModel sensors) {
		// checks if car is out of track
		if (sensors.getTrackPosition() < 1 && sensors.getTrackPosition() > -1) {
			// reading of sensor at +5 degree w.r.t. car axis
			float rxSensor = (float) sensors.getTrackEdgeSensors()[10];
			// reading of sensor parallel to car axis
			float sensorsensor = (float) sensors.getTrackEdgeSensors()[9];
			// reading of sensor at -5 degree w.r.t. car axis
			float sxSensor = (float) sensors.getTrackEdgeSensors()[8];

			float targetSpeed;

			// track is straight and enough far from a turn so goes to max speed
			if (sensorsensor > maxSpeedDist || (sensorsensor >= rxSensor && sensorsensor >= sxSensor))
				targetSpeed = maxSpeed;
			else {
				// approaching a turn on right
				if (rxSensor > sxSensor) {
					// computing approximately the "angle" of turn
					float h = sensorsensor * sin5;
					float b = rxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);
					// estimate the target speed depending on turn and on how close it is
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}
				// approaching a turn on left
				else {
					// computing approximately the "angle" of turn
					float h = sensorsensor * sin5;
					float b = sxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);
					// estimate the target speed depending on turn and on how close it is
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}

			}

			// accel/brake command is exponentially scaled w.r.t. the difference between
			// target speed and current one
			return (float) (2 / (1 + Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
			// return 1;
		} else
			return (float) 0.3; // when out of track returns a moderate acceleration command

	}
	

}
