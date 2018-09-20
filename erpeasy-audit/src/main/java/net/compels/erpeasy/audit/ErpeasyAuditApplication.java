package net.compels.erpeasy.audit;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.compels.erpeasy.audit.model.Endereco;
import net.compels.erpeasy.audit.model.Pessoa;

@SpringBootApplication
public class ErpeasyAuditApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ErpeasyAuditApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		testeDiff();

		testeRepository();

	}

	private void testeRepository() {
		Javers javers = JaversBuilder.javers().build();

		Pessoa vander = new Pessoa();
		vander.setId(1l);
		vander.setIdade(34);
		vander.setNome("Vander Silveira de Magalhaes");

		Endereco endereco = new Endereco();
		endereco.setId(1l);
		endereco.setLogadouro("Rua da Saudades");
		endereco.setNumero(442);
		List<Endereco> enderecos = new ArrayList<>();
		vander.setEnderecos(enderecos);
		vander.getEnderecos().add(endereco);

		javers.commit("Vander", vander);

		vander.setNome("Vander Silveira");

		vander.getEnderecos().get(0).setLogadouro("Rua Pedro Narciso Rodrigues");

		javers.commit("Vander", vander);

		Changes changes = javers
				.findChanges(QueryBuilder.byClass(Pessoa.class, Endereco.class).withNewObjectChanges().build());

		System.out.println("Printing Changes with grouping by commits and by objects :");
		changes.groupByCommit().forEach(byCommit -> {
			System.out.println("commit " + byCommit.getCommit().getId());
			byCommit.groupByObject().forEach(byObject -> {
				System.out.println("  changes on " + byObject.getGlobalId().value() + " : ");
				byObject.get().forEach(change -> {
					System.out.println("  - " + change);
				});
			});
		});

	}

	private void testeDiff() {
		Javers javers = JaversBuilder.javers().build();

		Pessoa vander = new Pessoa();
		vander.setId(1l);
		vander.setIdade(34);
		vander.setNome("Vander Silveira de Magalhaes");

		Endereco endereco = new Endereco();
		endereco.setId(1l);
		endereco.setLogadouro("Rua da Saudades");
		endereco.setNumero(442);
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		vander.setEnderecos(enderecos);

		Pessoa vander2 = new Pessoa();
		vander2.setId(1l);
		vander2.setIdade(35);
		vander2.setNome("Vander Silveira");

		endereco = new Endereco();
		endereco.setId(1l);
		endereco.setLogadouro("Rua Pedro Narciso Rodrigues");
		endereco.setNumero(442);
		enderecos = new ArrayList<>();
		enderecos.add(endereco);
		vander2.setEnderecos(enderecos);

		Diff diff = javers.compare(vander, vander2);
		System.out.println(diff);

		System.out.println("---------------------------------------");

		diff.getChanges().forEach(change -> System.out.println("- " + change));

		System.out.println("---------------------------------------");

		diff.groupByObject().forEach(byObject -> {
			System.out.println("* changes on " + byObject.getGlobalId().value() + " : ");
			byObject.get().forEach(change -> System.out.println("  - " + change));
		});

		System.out.println("---------------------------------------");

		System.out.println(javers.getJsonConverter().toJson(diff));
	}
}
