package perguntas;

public class VentoResposta implements RespostaInterface {
    @Override
    public String responder() {
        return "Ventos moderados soprando a 15 km/h na direção Norte.";
    }
}
