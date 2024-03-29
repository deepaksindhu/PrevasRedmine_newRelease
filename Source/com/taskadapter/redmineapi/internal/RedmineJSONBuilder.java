package com.taskadapter.redmineapi.internal;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taskadapter.redmineapi.RedmineInternalError;
import com.taskadapter.redmineapi.bean.Attachment;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueCategory;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Role;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.Version;
import com.taskadapter.redmineapi.internal.json.JsonObjectWriter;
import com.taskadapter.redmineapi.internal.json.JsonOutput;

//import org.json.JSONWriter;
//import org.json.JSONException;
//import org.json.JSONWriter;
import com.redmine.json.JSONWriter;
import com.redmine.json.JSONException;

/**
 * Builder for requests to Redmine in JSON format.
 */
public class RedmineJSONBuilder {

	private static JsonObjectWriter<Tracker> TRACKER_WRITER = new JsonObjectWriter<Tracker>() {
		@Override
		public void write(JSONWriter writer, Tracker object)
				throws JSONException {
			writeTracker(writer, object);
		}
	};

	public static JsonObjectWriter<Project> PROJECT_WRITER = new JsonObjectWriter<Project>() {
		@Override
		public void write(JSONWriter writer, Project object)
				throws JSONException {
			writeProject(writer, object);
		}
	};

	public static JsonObjectWriter<Issue> ISSUE_WRITER = new JsonObjectWriter<Issue>() {
		@Override
		public void write(JSONWriter writer, Issue object) throws JSONException {
			writeIssue(object, writer);
		}
	};

	public static JsonObjectWriter<User> USER_WRITER = new JsonObjectWriter<User>() {
		@Override
		public void write(JSONWriter writer, User object) throws JSONException {
			writeUser(object, writer);
		}
	};

	public static JsonObjectWriter<IssueRelation> RELATION_WRITER = new JsonObjectWriter<IssueRelation>() {
		@Override
		public void write(JSONWriter writer, IssueRelation object)
				throws JSONException {
			writeRelation(writer, object);
		}
	};

	public static JsonObjectWriter<IssueCategory> CATEGORY_WRITER = new JsonObjectWriter<IssueCategory>() {
		@Override
		public void write(JSONWriter writer, IssueCategory object)
				throws JSONException {
			writeCategory(object, writer);
		}
	};

	public static JsonObjectWriter<Version> VERSION_WRITER = new JsonObjectWriter<Version>() {
		@Override
		public void write(JSONWriter writer, Version object)
				throws JSONException {
			writeVersion(writer, object);
		}
	};

	public static JsonObjectWriter<TimeEntry> TIME_ENTRY_WRITER = new JsonObjectWriter<TimeEntry>() {
		@Override
		public void write(JSONWriter writer, TimeEntry object)
				throws JSONException {
			writeTimeEntry(writer, object);
		}
	};

	public static JsonObjectWriter<Attachment> UPLOAD_WRITER = new JsonObjectWriter<Attachment>() {
		@Override
		public void write(JSONWriter writer, Attachment object)
				throws JSONException {
			writeUpload(writer, object);
		}
	};

	public static JsonObjectWriter<Membership> MEMBERSHIP_WRITER = new JsonObjectWriter<Membership>() {
		@Override
		public void write(JSONWriter writer, Membership object)
				throws JSONException {
			writeMembership(writer, object);
		}
	};

	/**
	 * Writes a "create project" request.
	 * 
	 * @param writer
	 *            project writer.
	 * @param project
	 *            project to create.
	 * @throws IllegalArgumentException
	 *             if some project fields are not configured.
	 * @throws JSONException
	 *             if IO error occurs.
	 */
	public static void writeProject(JSONWriter writer, Project project)
			throws IllegalArgumentException, JSONException {
		/* Validate project */
		if (project.getName() == null)
			throw new IllegalArgumentException(
					"Project name must be set to create a new project");
		if (project.getIdentifier() == null)
			throw new IllegalArgumentException(
					"Project identifier must be set to create a new project");

		writeProject(project, writer);
	}

	static void writeTimeEntry(JSONWriter writer, TimeEntry timeEntry)
			throws JSONException {
		JsonOutput.addIfNotNull(writer, "id", timeEntry.getId());
		JsonOutput.addIfNotNull(writer, "project_id", timeEntry.getProjectId());
		JsonOutput.addIfNotNull(writer, "issue_id", timeEntry.getIssueId());
		JsonOutput.addIfNotNull(writer, "user_id", timeEntry.getUserId());
		JsonOutput.addIfNotNull(writer, "activity_id",
				timeEntry.getActivityId());
		JsonOutput.addIfNotNull(writer, "hours", timeEntry.getHours());
		JsonOutput.addIfNotNull(writer, "comment", timeEntry.getComment());
		addIfNotNullShort(writer, "spent_on", timeEntry.getSpentOn());
		addIfNotNullFull(writer, "created_on", timeEntry.getSpentOn());
		addIfNotNullFull(writer, "updated_on", timeEntry.getSpentOn());
	}

	/**
	 * Writes a tracker.
	 * 
	 * @param writer
	 *            used writer.
	 * @param tracker
	 *            tracker to writer.
	 * @throws JSONException
	 *             if error occurs.
	 */
	static void writeTracker(JSONWriter writer, Tracker tracker)
			throws JSONException {
		writer.key("id");
		writer.value(tracker.getId());
		writer.key("name");
		writer.value(tracker.getName());
	}

	static void writeRelation(JSONWriter writer, IssueRelation relation)
			throws JSONException {
		JsonOutput.addIfNotNull(writer, "issue_to_id", relation.getIssueToId());
		JsonOutput.addIfNotNull(writer, "relation_type", relation.getType());
		JsonOutput.addIfNotNull(writer, "delay", relation.getDelay());
	}

	static void writeVersion(JSONWriter writer, Version version)
			throws JSONException {
		JsonOutput.addIfNotNull(writer, "id", version.getId());
		if (version.getProject() != null)
			JsonOutput.addIfNotNull(writer, "project_id", version.getProject()
					.getId());
		JsonOutput.addIfNotNull(writer, "name", version.getName());
		JsonOutput
				.addIfNotNull(writer, "description", version.getDescription());
		JsonOutput.addIfNotNull(writer, "status", version.getStatus());
		addIfNotNullShort(writer, "due_date", version.getDueDate());
		addIfNotNullFull(writer, "created_on", version.getCreatedOn());
		addIfNotNullFull(writer, "updated_on", version.getUpdatedOn());
	}

	/**
	 * Converts object to a "simple" json.
	 * 
	 * @param tag
	 *            object tag.
	 * @param object
	 *            object to convert.
	 * @param writer
	 *            object writer.
	 * @return object String representation.
	 * @throws RedmineInternalError
	 *             if conversion fails.
	 */
	public static <T> String toSimpleJSON(String tag, T object,
			JsonObjectWriter<T> writer) throws RedmineInternalError {
		final StringWriter swriter = new StringWriter();
		final JSONWriter jsWriter = new JSONWriter(swriter);
		try {
			jsWriter.object();
			jsWriter.key(tag);
			jsWriter.object();
			writer.write(jsWriter, object);
			jsWriter.endObject();
			jsWriter.endObject();
		} catch (JSONException e) {
			throw new RedmineInternalError("Unexpected JSONException", e);
		}

		return swriter.toString();
	}

	public static void writeProject(Project project, final JSONWriter writer)
			throws JSONException {
		JsonOutput.addIfNotNull(writer, "id", project.getId());
		JsonOutput.addIfNotNull(writer, "identifier", project.getIdentifier());
		JsonOutput.addIfNotNull(writer, "name", project.getName());
		JsonOutput
				.addIfNotNull(writer, "description", project.getDescription());
		JsonOutput.addIfNotNull(writer, "homepage", project.getHomepage());
		addIfNotNullFull(writer, "created_on", project.getCreatedOn());
		addIfNotNullFull(writer, "updated_on", project.getUpdatedOn());
		JsonOutput.addIfNotNull(writer, "parent_id", project.getParentId());
		JsonOutput.addArrayIfNotNull(writer, "trackers", project.getTrackers(),
				TRACKER_WRITER);
	}

	public static void writeCategory(IssueCategory category,
			final JSONWriter writer) throws JSONException {
		writer.key("id");
		writer.value(category.getId());
		JsonOutput.addIfNotNull(writer, "name", category.getName());
		if (category.getProject() != null)
			JsonOutput.addIfNotNull(writer, "project_id", category.getProject()
					.getId());
		if (category.getAssignee() != null)
			JsonOutput.addIfNotNull(writer, "assigned_to_id", category
					.getAssignee().getId());
	}

	public static void writeUser(User user, final JSONWriter writer)
			throws JSONException {
		JsonOutput.addIfNotNull(writer, "id", user.getId());
		JsonOutput.addIfNotNull(writer, "login", user.getLogin());
		JsonOutput.addIfNotNull(writer, "password", user.getPassword());
		JsonOutput.addIfNotNull(writer, "firstname", user.getFirstName());
		JsonOutput.addIfNotNull(writer, "lastname", user.getLastName());
		JsonOutput.addIfNotNull(writer, "name", user.getFullName());
		JsonOutput.addIfNotNull(writer, "mail", user.getMail());
		addIfNotNullFull(writer, "created_on", user.getCreatedOn());
		addIfNotNullFull(writer, "last_login_on", user.getLastLoginOn());
		writeCustomFields(writer, user.getCustomFields());

	}

	public static void writeIssue(Issue issue, final JSONWriter writer)
			throws JSONException {
		JsonOutput.addIfNotNull(writer, "id", issue.getId());
		JsonOutput.addIfNotNull(writer, "subject", issue.getSubject());
		JsonOutput.addIfNotNull(writer, "parent_issue_id", issue.getParentId());
		JsonOutput.addIfNotNull(writer, "estimated_hours",
				issue.getEstimatedHours());
		JsonOutput.addIfNotNull(writer, "spent_hours", issue.getSpentHours());
		if (issue.getAssignee() != null)
			JsonOutput.addIfNotNull(writer, "assigned_to_id", issue
					.getAssignee().getId());
		JsonOutput.addIfNotNull(writer, "priority_id", issue.getPriorityId());
		JsonOutput.addIfNotNull(writer, "done_ratio", issue.getDoneRatio());
		if (issue.getProject() != null)
			JsonOutput.addIfNotNull(writer, "project_id", issue.getProject()
					.getIdentifier());
		if (issue.getAuthor() != null)
			JsonOutput.addIfNotNull(writer, "author_id", issue.getAuthor()
					.getId());
		addFull(writer, "start_date", issue.getStartDate());
		//addIfNotNullFull(writer, "start_date", issue.getStartDate());
		addIfNotNullFull(writer, "due_date", issue.getDueDate());
		if (issue.getTracker() != null)
			JsonOutput.addIfNotNull(writer, "tracker_id", issue.getTracker()
					.getId());
		JsonOutput.addIfNotNull(writer, "description", issue.getDescription());

		addIfNotNullFull(writer, "created_on", issue.getCreatedOn());
		addIfNotNullFull(writer, "updated_on", issue.getUpdatedOn());
		JsonOutput.addIfNotNull(writer, "status_id", issue.getStatusId());
		if (issue.getTargetVersion() != null)
			JsonOutput.addIfNotNull(writer, "fixed_version_id", issue
					.getTargetVersion().getId());
		if (issue.getCategory() != null)
			JsonOutput.addIfNotNull(writer, "category_id", issue.getCategory()
					.getId());
		JsonOutput.addIfNotNull(writer, "notes", issue.getNotes());
		writeCustomFields(writer, issue.getCustomFields());

		if (issue.getAttachments() != null && issue.getAttachments().size() > 0) {
			final List<Attachment> uploads = new ArrayList<Attachment>();
			for (Attachment attach : issue.getAttachments())
				if (attach.getToken() != null)
					uploads.add(attach);
			JsonOutput.addArrayIfNotEmpty(writer, "uploads", uploads,
					UPLOAD_WRITER);
		}
		

		/*
		 * Journals and Relations cannot be set for an issue during creation or
		 * updates.
		 */
	}

	public static void writeUpload(JSONWriter writer, Attachment attachment)
			throws JSONException {
		JsonOutput.addIfNotNull(writer, "token", attachment.getToken());
		JsonOutput.addIfNotNull(writer, "filename", attachment.getFileName());
		JsonOutput.addIfNotNull(writer, "content_type",
				attachment.getContentType());
	}

	public static void writeMembership(JSONWriter writer, Membership membership)
			throws JSONException {
		if (membership.getUser() != null)
			JsonOutput.addIfNotNull(writer, "user_id", membership.getUser()
					.getId());
		if (membership.getRoles() != null) {
			writer.key("role_ids");
			writer.array();
			for (Role role : membership.getRoles()) {
				writer.value(role.getId().longValue());
			}
			writer.endArray();
		}
	}

	private static void writeCustomFields(JSONWriter writer,
			List<CustomField> customFields) throws JSONException {
		if (customFields == null || customFields.isEmpty())
			return;
		writer.key("custom_field_values").object();
		for (CustomField field : customFields) {
			writer.key(Integer.toString(field.getId())).value(field.getValue());
		}
		writer.endObject();
	}

	/**
	 * Adds a value to a writer if value is not <code>null</code>.
	 * 
	 * @param writer
	 *            writer to add object to.
	 * @param field
	 *            field name to set.
	 * @param value
	 *            field value.
	 * @throws JSONException
	 *             if io error occurs.
	 */
	public static void addIfNotNullFull(JSONWriter writer, String field,
			Date value) throws JSONException {
		final SimpleDateFormat format = RedmineDateUtils.FULL_DATE_FORMAT.get();
		JsonOutput.addIfNotNull(writer, field, value, format);
	}

	/**
	 * Adds a value to a writer.
	 * 
	 * @param writer
	 *            writer to add object to.
	 * @param field
	 *            field name to set.
	 * @param value
	 *            field value.
	 * @throws JSONException
	 *             if io error occurs.
	 */
	public static void addFull(JSONWriter writer, String field, Date value)
			throws JSONException {
		final SimpleDateFormat format = RedmineDateUtils.FULL_DATE_FORMAT.get();
		//final SimpleDateFormat format = RedmineDateUtils.SHORT_DATE_FORMAT_V2.get();
		JsonOutput.add(writer, field, value, format);
	}

	/**
	 * Adds a value to a writer if value is not <code>null</code>.
	 * 
	 * @param writer
	 *            writer to add object to.
	 * @param field
	 *            field name to set.
	 * @param value
	 *            field value.
	 * @throws JSONException
	 *             if io error occurs.
	 */
	public static void addIfNotNullShort(JSONWriter writer, String field,
			Date value) throws JSONException {
		final SimpleDateFormat format = RedmineDateUtils.SHORT_DATE_FORMAT
				.get();
		JsonOutput.addIfNotNull(writer, field, value, format);
	}
}
