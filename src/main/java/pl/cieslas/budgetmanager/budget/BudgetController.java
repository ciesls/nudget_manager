package pl.cieslas.budgetmanager.budget;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.cieslas.budgetmanager.category.Category;
import pl.cieslas.budgetmanager.category.CategoryService;
import pl.cieslas.budgetmanager.updates.Updates;
import pl.cieslas.budgetmanager.user.CurrentUser;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final Updates updatesService;

    public BudgetController(BudgetService budgetService, CategoryService categoryService, Updates updatesService) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.updatesService = updatesService;
    }

    //  show budget form
    @GetMapping("/add")
    public String addBudgetForm(Model model) {
        model.addAttribute("budget", new Budget());
        return "budget/budgetAddForm";
    }

    //  add new budget
    @PostMapping("/add")
    public String addBudget(@Valid Budget budget, BindingResult result,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        if (result.hasErrors()){
            return "budget/budgetAddForm";
        }
        budget.setUser(currentUser.getUser());
        budgetService.saveBudget(budget);
        return "redirect:/budgets/all";
    }

    //  get all users details
    @GetMapping("/all")
    public String getUserBudgets(@AuthenticationPrincipal CurrentUser currentUser, Model model) {
        model.addAttribute("budgets", budgetService.findAllByUser(currentUser.getUser()));
        return "budget/userBudgets";
    }

    //    show form for editing budget
    @GetMapping("/edit/{id}")
    public String editBudgetForm(Model model, @PathVariable long id) {
        model.addAttribute("budget", budgetService.findById(id));
        return "budget/budgetEditForm";
    }

    //    edit budget
    @PostMapping("/edit/{id}")
    public String editBudget(Budget budget, @AuthenticationPrincipal CurrentUser currentUser) {
        budget.setUser(currentUser.getUser());
        budgetService.saveBudget(budget);
        return "redirect:/budgets/all";
    }

    @GetMapping("/details/{id}")
    public String getBudgeDetails(@PathVariable long id,
                                  @AuthenticationPrincipal CurrentUser currentUser, Model model) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate now = LocalDate.now();
        Optional<Budget> budget = budgetService.findByUserAndIdOrderByAmountDesc(currentUser.getUser(), id);
//        move to DTO
        budget.ifPresent(value -> model.addAttribute("budgetDetails", value));
//      get categories from budget

        List<Category> budgetCategories = categoryService.findAllByUserAndBudget(currentUser.getUser(), budget.get());
        model.addAttribute("categoriesBudget", budgetCategories);
//      get sum of expenses in budget in current month
        model.addAttribute("budgetSum",
                budgetService.calculateExpensesInBudgetDates(budgetCategories, currentUser.getUser(), monthStart, now));

        return "budget/userBudgetsDetails";
    }

    //    show categories in budget
    @GetMapping("/budgetCategories/{id}")
    public String getAllCategoriesFromBudget(@AuthenticationPrincipal CurrentUser currentUser,
                                             @PathVariable long id, Model model) {
        Optional<Budget> budget = budgetService.findById(id);
        List<Category> budgetCategories = categoryService.findAllByUserAndBudget(currentUser.getUser(), budget.get());
        model.addAttribute("categoriesBudget", budgetCategories);

        return "categories/userCategoriesBudgets";
    }

    @GetMapping("/budgetExpenses/{id}")
    public String getAllExpensesFromBudget(@AuthenticationPrincipal CurrentUser currentUser, Model model,
                                           @PathVariable long id) {
        Optional<Budget> budget = budgetService.findById(id);
        List<Category> budgetCategories = categoryService.findAllByUserAndBudget(currentUser.getUser(), budget.get());
        model.addAttribute("budgetExpenses",
                budgetService.getBudgetExpenses(budgetCategories, currentUser.getUser()));
        return "budget/userBudgetExpenses";
    }

    //    delete budget and set its categories do Other
    @GetMapping("/delete/{id}")
    public String deleteBudget(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable long id) {
        Optional<Budget> budget = budgetService.findById(id);
        List<Category> categoriesBudget = categoryService.findAllByUserAndBudget(currentUser.getUser(), budget.get());
        updatesService.setBudgetOther(budget.get(), categoriesBudget, currentUser.getUser());
        budgetService.deleteById(id);
        return "redirect:/budgets/all";
    }
}
