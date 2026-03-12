import pandas as pd
import numpy as np

# generate simple example dataset
data = {
    "student_id": range(1, 21),
    "enrollment_year": np.random.randint(2020, 2024, 20),
    "num_modules": np.random.randint(3, 7, 20),
    "num_marks": np.random.randint(3, 7, 20),
    "min_score": np.random.uniform(5, 12, 20),
    "max_score": np.random.uniform(10, 20, 20),
    "num_reports": np.random.randint(0, 3, 20),
}

df = pd.DataFrame(data)

# compute average score
df["avg_score"] = (df["min_score"] + df["max_score"]) / 2

# create binary label
df["at_risk"] = (df["avg_score"] < 10).astype(int)

# save dataset
df.to_csv("data/student_risk_dataset.csv", index=False)

print("Dataset created successfully!")