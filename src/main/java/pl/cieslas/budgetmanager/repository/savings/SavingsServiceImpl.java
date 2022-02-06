package pl.cieslas.budgetmanager.repository.savings;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cieslas.budgetmanager.entity.Savings;
import pl.cieslas.budgetmanager.entity.User;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@Transactional
public class SavingsServiceImpl implements SavingsService{

    private final SavingsRepository savingsRepository;

    public SavingsServiceImpl(SavingsRepository savingsRepository) {
        this.savingsRepository = savingsRepository;
    }

    @Override
    public Optional<Savings> findById(Long id) {
        return savingsRepository.findById(id);
    }

    @Override
    public Optional<Savings> findByIdAndUser(Long id, User user) {
        return savingsRepository.findByIdAndUser(id, user);
    }

    @Override
    public List<Savings> findAllByUser(User user) {
        return savingsRepository.findAllByUser(user);
    }

    @Override
    public Savings save(Savings savings) {
         return savingsRepository.save(savings);
    }

    @Override
    public void deleteById(Long id) {
        savingsRepository.deleteById(id);

    }
}
