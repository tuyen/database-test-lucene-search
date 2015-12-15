
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LuceneServlet
 */
@WebServlet("/LuceneServlet")
public class LuceneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	LuceneTest lc = new LuceneTest();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LuceneServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		// lc.createIndex();

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		request.getRequestDispatcher("home.jsp").include(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		String query = request.getParameter("search_lucene");
		List<String> results = lc.search(query);

		if (results.size() != 0) {
			for (String s : results) {
				response.getWriter().write(s + "<br>");
			}
		} else
			response.getWriter().write("NOT FOUND");
	}

}
