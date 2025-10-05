import com.forgegrid.service.UserService;

public class TestSaveOnboarding {
    public static void main(String[] args) {
        System.out.println("=== Testing Save Onboarding ===");
        
        UserService userService = new UserService();
        
        // Test saving onboarding data for the first user
        String username = "keerthanagpillai@gmail.com";
        String goal = "Learn programming fundamentals";
        String language = "Java";
        String skill = "Beginner (just starting out)";
        
        System.out.println("Attempting to save onboarding data for: " + username);
        System.out.println("  Goal: " + goal);
        System.out.println("  Language: " + language);
        System.out.println("  Skill: " + skill);
        
        boolean saved = userService.saveOnboardingDataByUsername(username, goal, language, skill);
        
        if (saved) {
            System.out.println("✓ Save successful!");
            
            // Verify the save
            System.out.println("\nVerifying saved data:");
            boolean completed = userService.hasCompletedOnboardingByUsername(username);
            System.out.println("  Onboarding completed: " + completed);
            
            String[] data = userService.getOnboardingDataByUsername(username);
            if (data != null) {
                System.out.println("  Goal: " + data[0]);
                System.out.println("  Language: " + data[1]);
                System.out.println("  Skill: " + data[2]);
            }
        } else {
            System.err.println("✗ Save failed!");
        }
    }
}
