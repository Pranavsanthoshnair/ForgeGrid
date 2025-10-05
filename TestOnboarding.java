import com.forgegrid.db.DatabaseHelper;
import com.forgegrid.service.UserService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestOnboarding {
    public static void main(String[] args) {
        System.out.println("=== Testing Onboarding Database ===");
        
        try {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance();
            Connection conn = dbHelper.getConnection();
            
            // Check table structure
            System.out.println("\n1. Checking users table structure:");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)");
            while (rs.next()) {
                System.out.println("  Column: " + rs.getString("name") + " (" + rs.getString("type") + ")");
            }
            
            // Check existing users
            System.out.println("\n2. Checking existing users:");
            rs = stmt.executeQuery("SELECT id, username, onboarding_completed, onboarding_goal, onboarding_language, onboarding_skill FROM users");
            while (rs.next()) {
                System.out.println("  User ID: " + rs.getInt("id"));
                System.out.println("    Username: " + rs.getString("username"));
                System.out.println("    Onboarding Completed: " + rs.getInt("onboarding_completed"));
                System.out.println("    Goal: " + rs.getString("onboarding_goal"));
                System.out.println("    Language: " + rs.getString("onboarding_language"));
                System.out.println("    Skill: " + rs.getString("onboarding_skill"));
                System.out.println();
            }
            
            // Test UserService
            System.out.println("3. Testing UserService:");
            UserService userService = new UserService();
            
            // Get first user to test
            rs = stmt.executeQuery("SELECT username FROM users LIMIT 1");
            if (rs.next()) {
                String username = rs.getString("username");
                System.out.println("  Testing with user: " + username);
                boolean completed = userService.hasCompletedOnboardingByUsername(username);
                System.out.println("  Has completed onboarding: " + completed);
                
                String[] data = userService.getOnboardingDataByUsername(username);
                if (data != null) {
                    System.out.println("  Onboarding data: [" + data[0] + ", " + data[1] + ", " + data[2] + "]");
                } else {
                    System.out.println("  No onboarding data found");
                }
            }
            
            System.out.println("\n=== Test Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
