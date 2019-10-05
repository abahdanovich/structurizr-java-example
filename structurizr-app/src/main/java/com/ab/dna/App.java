package com.ab.dna;

import java.io.File;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Enterprise;
import com.structurizr.model.Location;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.PaperSize;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

/**
 * This is a simple example of how to get started with Structurizr for Java.
 */
public class App {

    public static void main(String[] args) throws Exception {
      Workspace workspace = new Workspace("Bankowość", "Nasz model systemu");
      Model model = workspace.getModel();
      ViewSet views = workspace.getViews();

      model.setEnterprise(new Enterprise("Bankowość korporacyjna"));
      Person customer = model.addPerson(
        Location.External,
        "Klient indywidualny",
        "Klient banku, posiada konta bankowe z numerami"
      );

      SoftwareSystem internetBankingSystem = model.addSoftwareSystem(
        Location.Internal,
        "Platforma bankowości online",
        "Pozwala na przeglądanie stanu konta i dokonywanie transakcji"
      );

      customer.uses(internetBankingSystem, "Używa");
      SoftwareSystem mainframeBankingSystem = model.addSoftwareSystem(
        Location.Internal,
        "System bankowy Mainframe",
        "Core'owy system bankowy."
      );

      internetBankingSystem.uses(
        mainframeBankingSystem,
        "Pobiera informacje/Wysyła informacje"
      );

      SoftwareSystem emailSystem = model.addSoftwareSystem(
        Location.Internal,
        "E-mail System",
        "System e-mailowy Microsoft Exchange"
      );

      internetBankingSystem.uses(emailSystem, "Wysyła e-maile używając");
      emailSystem.delivers(customer, "Wysyła e-maile do");
      internetBankingSystem.uses(
        mainframeBankingSystem,
        "Pobiera informacje/Wysyła informacje"
      );

      Container mobileApp = internetBankingSystem.addContainer(
        "Aplikacja mobilna",
        "Dostarcza ograniczoną funkcjonalność bankowości online dla klientów",
        "Xamarin"
      );

      Container apiApplication = internetBankingSystem.addContainer(
        "API",
        "Dostarcza funkcjonalność bankowości online poprzez JSON/HTTPS API.",
        "Java i Spring MVC"
      );

      Container database = internetBankingSystem.addContainer(
        "Baza danych", "Informacje o klientach, hashowane hasła, logi, etc",
        "Relacyjna baza danych"
      );
      customer.uses(mobileApp, "Używa", "");

      apiApplication.uses(database, "Czyta/Zapisuje", "JDBC");
      apiApplication.uses(mainframeBankingSystem, "Używa", "XML/HTTPS");
      apiApplication.uses(emailSystem, "Wysyła maile", "SMTP");

      Component signinController = apiApplication.addComponent(
        "Logowanie",
        "Pozwala na logowanie do platformy bankowej",
        "Spring MVC Rest Controller"
      );
      Component accountsSummaryController = apiApplication.addComponent(
        "Konta bankowe",
        "Pozwala na wgląd w widok posiadanych kont",
        "Spring MVC Rest Controller"
      );
      Component resetPasswordController = apiApplication.addComponent(
        "Resetowanie hasła",
        "Pozwala na wygenerowanie URL do resetu hasła",
        "Spring MVC Rest Controller"
      );
      Component securityComponent = apiApplication.addComponent(
        "Komponent bezpieczeństwa",
        "Dostarcza funkcjonalność potrzebną do zmian hasła",
        "Spring Bean"
      );
      signinController.uses(securityComponent, "Używa");
      resetPasswordController.uses(securityComponent, "Używa");
      securityComponent.uses(database, "Czyta z i zapisuje do", "JDBC");
      model.addImplicitRelationships();


      SystemContextView systemContextView = views.createSystemContextView(
        internetBankingSystem,
        "SystemContext",
        "Diagram kontekstowy systemu bankowego"
      );
      systemContextView.addNearestNeighbours(internetBankingSystem);
      systemContextView.addAnimation(internetBankingSystem);
      systemContextView.addAnimation(customer);
      systemContextView.addAnimation(mainframeBankingSystem);
      systemContextView.addAnimation(emailSystem);

      ContainerView containerView = views.createContainerView(
        internetBankingSystem,
        "Containers",
        "Diagram kontenerów systemu Platformy Bankowowości Online"
      );
      containerView.add(customer);
      containerView.addAllContainers();
      containerView.add(mainframeBankingSystem);
      containerView.add(emailSystem);
      containerView.addAnimation(
        customer,
        mainframeBankingSystem,
        emailSystem
      );
      containerView.addAnimation(mobileApp);
      containerView.addAnimation(apiApplication);
      containerView.addAnimation(database);

      ComponentView componentView = views.createComponentView(
        apiApplication,
        "Components",
        "Diagram komponentów aplikacji API"
      );
      componentView.add(mobileApp);
      componentView.add(database);
      componentView.addAllComponents();
      componentView.add(mainframeBankingSystem);
      componentView.add(emailSystem);
      componentView.setPaperSize(PaperSize.A5_Landscape);
      componentView.addAnimation(mobileApp);
      componentView.addAnimation(
        signinController,
        securityComponent,
        database
      );
      componentView.addAnimation(
        accountsSummaryController,
        mainframeBankingSystem
      );
      componentView.addAnimation(
        resetPasswordController,
        emailSystem,
        database
      );

      uploadWorkspaceToStructurizr(workspace);
    }

    private static void uploadWorkspaceToStructurizr(Workspace workspace) throws Exception {
      long WORKSPACE_ID = Long.parseLong(System.getenv("STRUCTURIZR_WORKSPACE_ID"));
      String API_KEY = System.getenv("STRUCTURIZR_API_KEY");
      String API_SECRET = System.getenv("STRUCTURIZR_API_SECRET");

      StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
      structurizrClient.setWorkspaceArchiveLocation(new File("archived"));
      structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
    }

}
