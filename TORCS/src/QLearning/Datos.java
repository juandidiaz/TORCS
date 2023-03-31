package QLearning;

public class Datos {

	public final static float[] direccion = { 0.0f,-0.3f,0.3f };

	public final static int numero_estados = 273;
	public final static int num_angulos = direccion.length;

	public final static float[][] accel_freno = { {0f, .1f}, {0f, .2f}, {0f, .4f}, {0f, .8f}, {0f, 1f}, 
			{0f, 0f}, {0.1f, 0f}, {0.2f, 0f}, {0.4f, 0f}, {0.8f, 0f}, {1f, 0f}};

	public final static int accel_length = accel_freno.length;
	public final static int num_estados_velocidad = 11;

	private Datos() {

	}

}
